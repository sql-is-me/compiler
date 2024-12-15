package Midend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Children.Tools;
import Midend.M_utils.Operands;
import SymbolTable.FuncSymbol;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.VarSymbol.VarTypes;
import SymbolTable.utils;
import SymbolTable.FuncSymbol.FuncTypes;

public class MidCodeGenerate {
    /** 存储中端代码 */
    public static ArrayList<String> midcode = new ArrayList<>();

    public static void generateMidCode(int mainOffset) {
        addLibFunc(mainOffset);
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
                    "declare void @putstr(i8*)\n\n");
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

    /** 当前符号表 */
    public static SymTab recordSymTab = cur_symTab;

    /** Token合集 */
    public static ArrayList<Token> allTokens = (ArrayList<Token>) Lexer.tokens;

    /** Token ptr */
    public static int pos = 0;

    /** 寄存器号 */
    public static int regNO = 0;

    /** 记录寄存器号 */
    public static int recoderRegNum;

    /**
     * 添加库函数至全局符号表末尾
     * 
     */
    public static void addLibFunc(int mainOffset) {
        Symbol main = new FuncSymbol(global_symTab.id, "main", FuncTypes.IntFunc, new ArrayList<VarTypes>(), 0, -1,
                mainOffset);
        global_symTab.curSymTab.put("main", main);

        Symbol getint = new FuncSymbol(global_symTab.id, "getint", FuncTypes.IntFunc, new ArrayList<VarTypes>(), 0, -1,
                0);
        global_symTab.curSymTab.put("getint", getint);

        Symbol getchar = new FuncSymbol(global_symTab.id, "getchar", FuncTypes.IntFunc, new ArrayList<VarTypes>(), 0,
                -1, 0);
        global_symTab.curSymTab.put("getchar", getchar);

        Symbol putint = new FuncSymbol(global_symTab.id, "putint", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.Int)), 1, -1, 0);
        global_symTab.curSymTab.put("putint", putint);

        Symbol putch = new FuncSymbol(global_symTab.id, "putch", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.Int)), 1, -1, 0);
        global_symTab.curSymTab.put("putch", putch);

        Symbol putstr = new FuncSymbol(global_symTab.id, "putstr", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.CharArray)), 1, -1, 0);
        global_symTab.curSymTab.put("putstr", putstr);
    }

    /*
     * 全局变量和函数代码生成
     */
    public static void addGlobalVarandFunc() {
        LinkedHashMap<String, Symbol> currentSymTab = global_symTab.curSymTab;

        for (Map.Entry<String, Symbol> entry : currentSymTab.entrySet()) {
            Symbol symbol = entry.getValue();

            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                if (varSymbol.value.size() == 0) {
                    varSymbol.value = (M_utils.calExpsValue(varSymbol, varSymbol.valueExp));
                }
                MidCodeGenerate.addLinetoAns(returnGlobalVarsCode(varSymbol));
            } else {
                addLinetoAns("\n");
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                MidCodeGenerate.addLinetoAns(returnFuncsCode(funcSymbol));
                if (funcSymbol.name.equals("main")) {
                    break;
                }
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
            sb.append("i32 " + symbol.value.get(0) + "\n");
        } else if (symbol.type.equals(VarTypes.ConstChar) || (symbol.type.equals(VarTypes.Char))) {
            sb.append("i8 " + symbol.value.get(0) + "\n");
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
        StringBuilder sb = new StringBuilder();
        recoderRegNum = regNO;
        regNO = 0;
        String ret_String;

        sb.append("define dso_local ");

        if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
            sb.append("i32 ");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.CharFunc)) {
            sb.append("i8 ");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.VoidFunc)) {
            sb.append("void ");
        }

        sb.append("@" + funcSymbol.name + "(");

        int i = 0;
        for (VarSymbol.VarTypes paramstype : funcSymbol.paramTypes) {
            if (paramstype.equals(VarSymbol.VarTypes.Int)) {
                sb.append("i32 ");
            } else if (paramstype.equals(VarSymbol.VarTypes.Char)) {
                sb.append("i8 ");
            } else if (paramstype.equals(VarSymbol.VarTypes.IntArray)
                    || paramstype.equals(VarSymbol.VarTypes.CharArray)) {
                sb.append("ptr ");
            }

            sb.append("%" + regNO++);

            if (i != funcSymbol.paramTypes.size() - 1) {
                sb.append(", ");
                i++;
            }
        }

        sb.append(") {\n");
        addLinetoAns(sb.toString());

        regNO++;// 出函数定义句，寄存器+1

        M_utils.findFuncPosinTokens(funcSymbol);
        pos++; // {
        M_utils.jumpinChildSymTab();
        ret_String = generateBodyCode(regNO, funcSymbol);

        sb = new StringBuilder();

        if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.VoidFunc)) {
            sb.append("ret void");
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
            sb.append("ret i32 " + ret_String);
        } else if (funcSymbol.returnType.equals(FuncSymbol.FuncTypes.CharFunc)) {
            sb.append("ret i8 " + ret_String);
        }
        sb.append("\n}\n");

        regNO = recoderRegNum;

        return sb.toString();
    }

    /**
     * 函数体代码生成
     *
     * @param beginRegNO
     * @param funcSymbol
     * @return
     */
    public static String generateBodyCode(int beginRegNO, FuncSymbol funcSymbol_f) {
        SymTab funcSymTab = M_utils.findFuncSymTab(funcSymbol_f.mySymTabId);
        String returnRegNOorValue = "";
        StringBuilder sb = new StringBuilder();

        generateFuncInitCode(funcSymbol_f);

        int level = 1;
        while (level != 0 && allTokens.get(pos).str != "}") {
            if (allTokens.get(pos).str.equals("{")) {
                level++;
                M_utils.jumpinChildSymTab();
            } else if (allTokens.get(pos).str.equals("}")) {
                M_utils.jumpoutChildSymTab();
                level--;
            } else {
                Token t = Tools.GetCountTK(pos);
                if (t.tk.equals("IDENFR")) {
                    pos++;
                    if (Tools.GetCountTK(pos).str.equals("=")) { // 变量赋值
                        VarSymbol varSymbol = M_utils.findDefinedVarfromSymTab(t.str);
                        int begin = pos + 1;
                        M_utils.findEndofScope();
                        int end = pos - 1;

                        ArrayList<Token> expTokens = Tools.GetExpfromIndex(begin, end);// new
                                                                                       // ArrayList<ArrayList<Token>>(Arrays.asList(expTokens))

                        if (varSymbol.stackRegID == -1) {
                            throw new RuntimeException("generatebodycode栈寄存器号为-1");
                        }
                        funcSymbol_f.needAssignValueReg = AssignmentStatement(varSymbol, expTokens);

                    } else { // 函数调用
                        FuncSymbol funcSymbol = M_utils.findFuncSymbolfromSymTab(t.str);
                        M_utils.FuncRParams funcRParams = new M_utils.FuncRParams();
                        funcRParams.size = funcSymbol.paramNumber;

                        pos++; // ( + 1
                        ArrayList<Token> expTokens = new ArrayList<>();
                        int l = 1;
                        while (true) {
                            if (Tools.GetCountTK(pos).str.equals(")")) {
                                l--;
                            } else if (Tools.GetCountTK(pos).str.equals("(")) {
                                l++;
                            }
                            if (l == 0) {
                                pos++; // )
                                break;
                            }
                            expTokens.add(Tools.GetCountTK(pos));
                            pos++;
                        }
                        callFunc(funcSymbol, funcRParams, expTokens);
                    }

                } else if (t.tk.equals("CONSTTK")) {
                    pos++;
                } else if (t.tk.equals("INTTK") || t.tk.equals("CHARTK")) { // 变量声明
                    pos++; // identifier

                    int begin = pos;
                    M_utils.findEndofScope();
                    int end = pos - 1;

                    ArrayList<Token> exp = Tools.GetExpfromIndex(begin, end);

                    for (int i = 0; i < exp.size(); i++) {
                        Token tt = exp.get(i);
                        VarSymbol varSymbol = M_utils.findVarfromSymTab(tt.str);
                        i++;// identifier
                        if (i < exp.size())
                            tt = exp.get(i);
                        else {
                            DeclareLocalVariable(varSymbol, new ArrayList<>());
                            break;
                        }

                        if (tt.str.equals("=")) {
                            i++; // =
                            int l = 0;
                            ArrayList<Token> subexp = new ArrayList<>();

                            while (l != 0 || (i < exp.size() && !exp.get(i).str.equals(","))) {
                                if (exp.get(i).str.equals("(")) {
                                    level++;
                                } else if (exp.get(i).str.equals(")")) {
                                    level--;
                                }
                                subexp.add(exp.get(i));
                                i++;
                            }
                            DeclareLocalVariable(varSymbol, subexp);
                        } else {
                            DeclareLocalVariable(varSymbol, new ArrayList<>());
                        }
                    }
                } else if (t.tk.equals("PRINTFTK")) {
                    pos += 2; // ( + 1
                    generatePrintfCode();
                } else if (t.tk.equals("RETURNTK")) {
                    pos++;
                    Operands operands = calReturnExp();
                    if (operands != null) {
                        if (operands.kind == 0 || (operands.kind == 2 && operands.retRegNO == 0)) {
                            returnRegNOorValue = String.valueOf(operands.value);
                        } else {
                            if (funcSymbol_f.returnType.equals(FuncSymbol.FuncTypes.IntFunc)) {
                                if (operands.type == 0) {
                                    returnRegNOorValue = String.valueOf("%" + operands.retRegNO);
                                } else {
                                    returnRegNOorValue = String
                                            .valueOf("%" + M_utils.transTypetoInt(operands.retRegNO));
                                }
                            } else if (funcSymbol_f.returnType.equals(FuncSymbol.FuncTypes.CharFunc)) {
                                if (operands.type == 1) {
                                    returnRegNOorValue = String.valueOf("%" + operands.retRegNO);
                                } else {
                                    returnRegNOorValue = String
                                            .valueOf("%" + M_utils.transTypetoChar(operands.retRegNO));
                                }
                            }

                        }
                    }
                }
            }

            if (level == 0) {
                break;
            }
            pos++;
        }

        return returnRegNOorValue;
    }

    public static int callFunc(FuncSymbol funcSymbol, M_utils.FuncRParams funcRParams,
            ArrayList<Token> expTokens) {

        ArrayList<ArrayList<Token>> expsTokens = new ArrayList<>();
        int p = 0;
        for (int count = 0; count < funcSymbol.paramNumber; count++) {
            if (count < funcSymbol.paramNumber - 1) {
                ArrayList<Token> exp = new ArrayList<>();
                for (int level = 0; !expTokens.get(p).str.equals(",") || level != 0; p++) {
                    if (expTokens.get(p).str.equals("(")) {
                        level++;
                    } else if (expTokens.get(p).str.equals(")")) {
                        level--;
                    }
                    exp.add(expTokens.get(p));
                }
                p++; // ,
                expsTokens.add(exp);
            } else {
                ArrayList<Token> exp = new ArrayList<>();
                for (; p < expTokens.size(); p++) {
                    exp.add(expTokens.get(p));
                }
                expsTokens.add(exp);
            }
        }

        for (ArrayList<Token> exp : expsTokens) {
            Operands operand = M_utils.calExpValue(exp);
            if (operand.kind == 0) // 常值
            {
                funcRParams.value.add(operand.value);
                funcRParams.isConst.add(true);
                funcRParams.type.add(operand.type);
            }

            else if (operand.kind == 1) { // var
                if (operand.varSymbol.needAssignVReg) {
                    M_utils.AssignValueRegister(operand.varSymbol);

                    funcRParams.value.add(operand.varSymbol.valueRegID);
                    funcRParams.isConst.add(false);
                } else {
                    funcRParams.value.add(operand.varSymbol.valueRegID);
                    funcRParams.isConst.add(false);
                }

                funcRParams.type.add(operand.type);
            }

            else if (operand.kind == 2) { // 子表达式
                funcRParams.value.add(operand.retRegNO);
                funcRParams.isConst.add(false);
                funcRParams.type.add(operand.type);
            }
        }
        int retRegNO = M_utils.generateCallFuncCode(funcSymbol, funcRParams);
        M_utils.AssignGlobalVars(funcSymbol);

        return retRegNO;

    }

    /**
     * 局部变量声明代码生成
     * 
     * @param varSymbol
     * @param regManager
     * @return
     */
    public static void DeclareLocalVariable(VarSymbol varSymbol, ArrayList<Token> expTokens) {
        StringBuilder sb = new StringBuilder();

        varSymbol.stackRegID = regNO++; // 添加对应的寄存器号
        sb.append("%" + varSymbol.stackRegID + " = alloca ");
        if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)
                || varSymbol.type.equals(VarSymbol.VarTypes.Char)
                || varSymbol.type.equals(VarSymbol.VarTypes.ConstChar)) {
            if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                sb.append("i32\n");
            } else {
                sb.append("i8\n");
            }
            addLinetoAns(sb.toString());

            if (varSymbol.stackRegID == -1) {
                throw new RuntimeException("declareLocalVariable栈寄存器号为-1");
            }
            AssignmentStatement(varSymbol, expTokens);

        } else if (varSymbol.type.equals(VarSymbol.VarTypes.IntArray)
                || varSymbol.type.equals(VarSymbol.VarTypes.CharArray)) {
            if (varSymbol.type.equals(VarSymbol.VarTypes.IntArray)) {
                sb.append("[" + varSymbol.size + "x i32 ]");
            } else {
                sb.append("[" + varSymbol.size + "x i8 ]");
            }

            // TODO: 数组初始化
        }
        return;
    }

    public static ArrayList<String> AssignmentStatement(VarSymbol varSymbol, ArrayList<Token> valueExp) {
        StringBuilder sb = new StringBuilder();
        StringBuilder tsb = new StringBuilder();
        ArrayList<String> global_changeValue = new ArrayList<>();

        Operands operand = M_utils.calExpValue(valueExp);

        if (operand == null) {
            return null;
        }

        if (operand.kind == 0) { // 常值
            if (varSymbol.value.size() == 0) {
                varSymbol.value.add(operand.value);
            } else {
                varSymbol.value.set(0, operand.value);
            }
            tsb.append(operand.value);
        } else {
            if (varSymbol.type == VarSymbol.VarTypes.Int) {
                if (operand.type == 0) {
                    tsb.append("%" + operand.retRegNO);
                } else {
                    tsb.append("%" + M_utils.transTypetoInt(operand.retRegNO));
                }
            } else {
                if (operand.type == 1) {
                    tsb.append("%" + operand.retRegNO);
                } else {
                    tsb.append("%" + M_utils.transTypetoChar(operand.retRegNO));
                }
            }
        }
        varSymbol.needAssignVReg = true;

        if (varSymbol.tableId == 1) { // 全局变量
            if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                sb.append("store " + "i32 " + tsb.toString() + ", i32* @" + varSymbol.name);
            } else if (varSymbol.type.equals(VarSymbol.VarTypes.Char)
                    || varSymbol.type.equals(VarSymbol.VarTypes.ConstChar)) {
                sb.append("store " + "i8 " + tsb.toString() + ", i8* @" + varSymbol.name);
            }
            global_changeValue.add(varSymbol.name);
        } else {
            if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                sb.append("store " + "i32 " + tsb.toString() + ", i32* %" + varSymbol.stackRegID);
            } else if (varSymbol.type.equals(VarSymbol.VarTypes.Char)
                    || varSymbol.type.equals(VarSymbol.VarTypes.ConstChar)) {
                sb.append("store " + "i8 " + tsb.toString() + ", i8* %" + varSymbol.stackRegID);
            }
        }

        addLinetoAns(sb.toString());
        addLinetoAns("\n");

        return global_changeValue; // FIXME
    }

    public static void generatePrintfCode() {
        StringBuilder sb = new StringBuilder();
        Token t = Tools.GetCountTK(pos);
        pos++;// ) or ,
        if (!Tools.GetCountTK(pos).str.equals(")")) { //
            pos++;
        }
        String str = t.str;
        int size = str.length();
        Character cc;

        int count = getPrintfCount(str);
        Queue<Operands> printfParams = AnalysisPrintfRParms(count);

        for (int i = 1; i < size - 1; i++) {
            cc = str.charAt(i);
            if (cc.equals('%')) {
                if (i < size - 1 && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                    i++;
                    cc = str.charAt(i);
                    sb.append("call void @put");
                    if (cc == 'd') {
                        sb.append("int(i32 %");
                    } else {
                        sb.append("ch(i32 %");
                    }

                    Operands operands = printfParams.poll();
                    if (operands.kind == 0) {
                        sb.append(operands.value + ")\n");
                    } else if (operands.kind == 1) {
                        if (operands.type == 0) {
                            sb.append(operands.varSymbol.valueRegID + ")\n");
                        } else {
                            sb.append(M_utils.transTypetoInt(operands.varSymbol.valueRegID) + ")\n");
                        }
                    } else if (operands.kind == 2) {
                        if (operands.type == 0) {
                            sb.append(operands.retRegNO + ")\n");
                        } else {
                            sb.append(M_utils.transTypetoInt(operands.retRegNO) + ")\n");

                        }
                    }

                }
            } else {
                if (cc == '\\') {
                    if (str.charAt(i + 1) == 'n') {
                        sb.append("call void @putch(i32 10)\n");
                        i++;
                    }
                } else {
                    sb.append("call void @putch(i32 " + (int) cc + ")\n");
                }
            }
        }
        addLinetoAns(sb.toString());
    }

    public static Queue<Operands> AnalysisPrintfRParms(int count) {
        Queue<Operands> printfParams = new LinkedList<>();
        while (count != 0) {
            int begin = pos, end;
            if (count > 1) {
                int level = 0;
                while (level != 0 || !Tools.GetCountTK(pos).str.equals(",")) {
                    if (Tools.GetCountTK(pos).str.equals("(")) {
                        level++;
                    } else if (Tools.GetCountTK(pos).str.equals(")")) {
                        level--;
                    }
                    pos++;
                }
                end = pos - 1; // ,
                pos++;
            } else {
                M_utils.findEndofScope();
                end = pos - 2; // );
            }

            ArrayList<Token> expTokens = Tools.GetExpfromIndex(begin, end);
            Operands operand = M_utils.calExpValue(expTokens);
            printfParams.add(operand);

            count--;
        }

        return printfParams;
    }

    public static int getPrintfCount(String str) {
        int count = 0;
        int size = str.length();
        Character cc;

        for (int i = 0; i < size; i++) {
            cc = str.charAt(i);
            if (cc.equals('%')) {
                if (i < size - 1 && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                    i++;
                    cc = str.charAt(i);
                    if (cc == 'd' || cc == 'c') {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static Operands calReturnExp() {
        int begin = pos;
        while (!Tools.GetCountTK(pos).str.equals(";")) {
            pos++;
        }
        int end = pos - 1;

        if (end < begin) {
            return null;
        }
        ArrayList<Token> expTokens = Tools.GetExpfromIndex(begin, end);

        return M_utils.calExpValue(expTokens);
    }

    public static void generateFuncInitCode(FuncSymbol funcSymbol) { // FIXME:10问题
        StringBuilder sb;
        int count = 0;

        if (funcSymbol.paramNumber == 0) {
            return;
        }

        for (Map.Entry<String, Symbol> entry : cur_symTab.curSymTab.entrySet()) {
            VarSymbol varSymbol;
            int stackRegNO = regNO++;

            if (count < funcSymbol.paramNumber) {
                if (entry.getValue() instanceof VarSymbol) {
                    varSymbol = (VarSymbol) entry.getValue();
                    varSymbol.stackRegID = stackRegNO;
                    entry.setValue(varSymbol);
                }
            } else if (count >= funcSymbol.paramNumber - 1) {
                break;
            }

            sb = new StringBuilder();

            sb.append("%" + stackRegNO + " = alloca ");
            if (funcSymbol.paramTypes.get(count).equals(VarSymbol.VarTypes.Int)) {
                sb.append("i32\n");
            } else {
                sb.append("i8\n");
            }

            sb.append("store ");
            if (funcSymbol.paramTypes.get(count).equals(VarSymbol.VarTypes.Int)) {
                sb.append("i32 %" + String.valueOf(count) + ", i32* %" + stackRegNO);
            } else {
                sb.append("i8 %" + String.valueOf(count) + ", i8* %" + stackRegNO);
            }

            addLinetoAns(sb.toString());
            addLinetoAns("\n");

            count++;
        }

    }
}
