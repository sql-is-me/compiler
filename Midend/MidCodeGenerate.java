package Midend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.Lexer.Token;
import SymbolTable.FuncSymbol;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.VarSymbol.VarTypes;
import SymbolTable.utils;

public class MidCodeGenerate {
    /** 存储中端代码 */
    public static ArrayList<String> midcode = new ArrayList<>();

    public static void generateMidCode() {
        addGlobalVarandFunc();
    }

    public static void addLinetoAns(String code) {
        midcode.add(code);
    }

    public static void printfCodetoLL() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("llvm_ir.txt"))) {
            bw.write("declare i32 @getint()\n" +
                    "declare i32 @getchar()\n" +
                    "declare void @putint(i32)\n" +
                    "declare void @putch(i32)\n" +
                    "declare void @putstr(i8*)\n");
            for (String s : midcode) {
                bw.write(s);
            }
        } catch (IOException e) {
            System.err.println("could not open llvm_ir.txt");
        }
    }

    /* ------------------------------------------------------------- */

    /** 全局符号表 */
    public static SymTab global_symTab = utils.globalSymTab;

    /** 当前符号表 */
    public static SymTab cur_symTab = global_symTab;

    /** Token合集 */
    public static ArrayList<Token> allTokens = (ArrayList<Token>) Lexer.tokens;

    /** Token ptr */
    public static int pos = 0;

    /*
     * 全局变量和函数代码生成
     */
    public static void addGlobalVarandFunc() {
        LinkedHashMap<String, Symbol> currentSymTab = global_symTab.curSymTab;

        for (Map.Entry<String, Symbol> entry : currentSymTab.entrySet()) {
            Symbol symbol = entry.getValue();

            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                if (varSymbol.value == null) {
                    varSymbol.value = M_utils.calExpsValue(varSymbol.valueExp);
                }
                MidCodeGenerate.addLinetoAns(returnGlobalVarsCode(varSymbol));
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                MidCodeGenerate.addLinetoAns(returnFuncsCode(funcSymbol));
            }
        }
    }

    /**
     * 全局变量代码生成
     *
     * @param symbol
     * @return
     */
    public static String returnGlobalVarsCode(VarSymbol symbol) {
        StringBuilder sb = new StringBuilder();

        sb.append("@" + symbol.name + " = dso_local ");

        if (symbol.type.equals(VarTypes.ConstInt) || (symbol.type.equals(VarTypes.ConstChar))
                || (symbol.type.equals(VarTypes.ConstIntArray))
                || (symbol.type.equals(VarTypes.ConstCharArray))) {
            sb.append("constant ");
        } else if (symbol.type.equals(VarTypes.Int) || (symbol.type.equals(VarTypes.Char))
                || (symbol.type.equals(VarTypes.IntArray))
                || (symbol.type.equals(VarTypes.CharArray))) {
            sb.append("global ");
        }

        if (symbol.type.equals(VarTypes.ConstInt) || (symbol.type.equals(VarTypes.Int))) {
            sb.append("i32 ");
        } else if (symbol.type.equals(VarTypes.ConstChar) || (symbol.type.equals(VarTypes.Char))) {
            sb.append("i8 ");
        } else if (symbol.type.equals(VarTypes.ConstIntArray) || (symbol.type.equals(VarTypes.IntArray))) {
            sb.append("[" + symbol.size + "x i32 ]");

            if (symbol.zeroinitializer) {
                sb.append("zeroinitializer\n");
            } else {
                sb.append(" [");
                for (int i = 0; i < symbol.size; i++) {
                    sb.append("i32 " + symbol.value.get(i));
                    if (i != symbol.size - 1) {
                        sb.append(", ");
                    } else {
                        sb.append("] + '\n");
                    }
                }
            }

        } else if (symbol.type.equals(VarTypes.ConstCharArray) || (symbol.type.equals(VarTypes.CharArray))) {
            sb.append("[" + symbol.size + "x i8 ] ");

            if (symbol.zeroinitializer) {
                sb.append("zeroinitializer\n");
            } else {
                sb.append("c\"");
                for (int i = 0; i < symbol.size; i++) {
                    sb.append("i8 " + symbol.value.get(i));
                    if (i != symbol.size - 1) {
                        sb.append(", ");
                    } else {
                        sb.append("\" + '\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 函数代码生成
     * 
     * @param funcSymbol
     * @return
     */
    public static String returnFuncsCode(FuncSymbol funcSymbol) {
        RegisterManager regManager = new RegisterManager(funcSymbol.id);
        int ret_regNo;
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ");

        if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
            sb.append("i32 ");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.CharFunc)) {
            sb.append("i8 ");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.VoidFunc)) {
            sb.append("void ");
        }

        sb.append("@" + funcSymbol.name + "(");

        for (VarSymbol.VarTypes paramstype : funcSymbol.paramTypes) {
            if (paramstype.equals(VarSymbol.VarTypes.Int)) {
                sb.append("i32 ");
            } else if (paramstype.equals(VarSymbol.VarTypes.Char)) {
                sb.append("i8 ");
            } else if (paramstype.equals(VarSymbol.VarTypes.IntArray)
                    || paramstype.equals(VarSymbol.VarTypes.CharArray)) {
                sb.append("ptr ");
            }

            sb.append("%" + regManager.regNO++);
        }

        sb.append(") {\n");
        regManager.regNO++;// 出函数定义句，寄存器+1

        // TODO : 函数内部体
        // ret_regNo = returnBodyCode(regManager.regNO, funcSymbol);

        if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.VoidFunc)) {
            sb.append("ret void");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
            sb.append("ret i32 %" + ret_regNo);
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
            sb.append("ret i8 %" + ret_regNo);
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    /**
     * 函数体代码生成
     *
     * @param beginRegNO
     * @param funcSymbol
     * @return
     */
    public static Pair returnBodyCode(int beginRegNO, FuncSymbol funcSymbol) {
        SymTab funcSymTab = M_utils.findFuncSymTab(funcSymbol.symTabID);
        int returnRegNO = -1;
        StringBuilder sb = new StringBuilder();

        pos = funcSymbol.offset + 2; // ( + 2

        int level = 1;
        while (level == 0 && allTokens.get(pos).str != "}") {
            if (allTokens.get(pos).str.equals("{")) {
                level++;
            } else if (allTokens.get(pos).str.equals("}")) {
                level--;
            } else {
                // TODO: 函数体的处理
            }

            if (level == 0) {
                break;
            }
        }
        return new Pair(returnRegNO, sb.toString());
    }

    /**
     * 局部变量声明代码生成
     * 
     * @param varSymbol
     * @param regManager
     * @return
     */
    public static String DeclareLocalVariable(VarSymbol varSymbol, RegisterManager regManager) {
        StringBuilder sb = new StringBuilder();

        varSymbol.stackRegID = regManager.regNO; // 添加对应的寄存器号
        sb.append("%" + regManager.regNO++ + " = alloca ");
        if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)
                || varSymbol.type.equals(VarSymbol.VarTypes.Char)
                || varSymbol.type.equals(VarSymbol.VarTypes.ConstChar)) {
            if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                sb.append("i32\n");
            } else {
                sb.append("i8\n");
            }

            // TODO: 局部变量初始化计算exp

        } else if (varSymbol.type.equals(VarSymbol.VarTypes.IntArray)
                || varSymbol.type.equals(VarSymbol.VarTypes.CharArray)) {
            if (varSymbol.type.equals(VarSymbol.VarTypes.IntArray)) {
                sb.append("[" + varSymbol.size + "x i32 ]");
            } else {
                sb.append("[" + varSymbol.size + "x i8 ]");
            }

            // TODO: 数组初始化
        }

        return sb.toString();
    }

    public static String AssignmentStatement(VarSymbol varSymbol, ArrayList<Token> valueExp) { // TODO: 赋值语句
        StringBuilder sb = new StringBuilder();
        int valueRegNO;

        // TODO

        if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
            sb.append("store " + "i32" + valueRegNO + ", " + varSymbol.stackRegID + ", " + "\n");
        } else if (varSymbol.type.equals(VarSymbol.VarTypes.Char)) {
            sb.append("store " + +varSymbol.stackRegID + " = ");
        }
        return sb.toString();
    }

    public static Pair generateExpCode() {
        StringBuilder sb = new StringBuilder();
        int regNO = -1;

        
        return new Pair(regNO, sb.toString());
    }
}
