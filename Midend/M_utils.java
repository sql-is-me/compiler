package Midend;

import java.util.*;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Children.Tools;
import SymbolTable.FuncSymbol;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class M_utils {
    /** 全局符号表 */
    public static SymTab global_symTab = utils.globalSymTab;

    /** 当前符号表 */
    public static SymTab cur_symTab = global_symTab;

    /** Token合集 */
    public static ArrayList<Token> allTokens = (ArrayList<Token>) Lexer.tokens;

    /** Token ptr */
    public static int pos = 0;

    public static void findFuncPosinTokens() {

    }

    public static void addGlobalVarandFunc() {
        LinkedHashMap<String, Symbol> currentSymTab = global_symTab.curSymTab;

        for (Map.Entry<String, Symbol> entry : currentSymTab.entrySet()) {
            Symbol symbol = entry.getValue();

            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                if (varSymbol.value == null) {
                    varSymbol.value = calExpsValue(varSymbol.valueExp);
                }
                MidCodeGenerate.addLinetoAns(returnGlobalVarsCode(varSymbol));
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                MidCodeGenerate.addLinetoAns(returnFuncsCode(funcSymbol));
            }
        }
    }

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
            sb.append("[" + symbol.size + "x i32 ] [");

            for (int i = 0; i < symbol.size; i++) {
                sb.append("i32 " + symbol.value.get(i));
                if (i != symbol.size - 1) {
                    sb.append(", ");
                } else {
                    sb.append("] + '\n");
                }
            }
        } else if (symbol.type.equals(VarTypes.ConstCharArray) || (symbol.type.equals(VarTypes.CharArray))) {
            sb.append("[" + symbol.size + "x i8 ] c\"");

            for (int i = 0; i < symbol.size; i++) {
                sb.append("i8 " + symbol.value.get(i));
                if (i != symbol.size - 1) {
                    sb.append(", ");
                } else {
                    sb.append("\" + '\n");
                }
            }
        }
        return sb.toString();
    }

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

        // FIXME : 函数内部体

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
     * 
     *
     * @return
     */
    public static String returnBodyCode(int beginRegNO, FuncSymbol funcSymbol) {
        SymTab funcSymTab = findFuncSymTab(funcSymbol.symTabID);
        StringBuilder sb = new StringBuilder();

        pos = funcSymbol.offset + 2; // ( + 2

        int level = 1;
        while (level == 0 && allTokens.get(pos).str != "}") {
            if (allTokens.get(pos).str.equals("{")) {
                level++;
            } else if (allTokens.get(pos).str.equals("}")) {
                level--;
            } else {

            }

            if (level == 0) {
                break;
            }
        }

        return sb.toString();
    }

    public static ArrayList<Integer> calExpsValue(ArrayList<ArrayList<Token>> valueExp) {
        ArrayList<Integer> values = new ArrayList<>();

        for (ArrayList<Token> exp : valueExp) {
            values.add(calExpValue(exp));
        }
        return values;
    }

    /**
     * 查找对应变量符号
     *
     * @param ident
     * @return
     */
    public static VarSymbol findVarfromSymTab(String ident) {
        Symbol symbol = null;

        if (cur_symTab.curSymTab.containsKey(ident)) {
            symbol = cur_symTab.curSymTab.get(ident);
        } else {
            SymTab tempSymTab = cur_symTab;
            while (tempSymTab.lastSymTab != null) {
                tempSymTab = tempSymTab.lastSymTab;
                if (tempSymTab.curSymTab.containsKey(ident)) {
                    symbol = tempSymTab.curSymTab.get(ident);
                    break;
                }
            }
        }

        VarSymbol varSymbol = (VarSymbol) symbol;
        return varSymbol;
    }

    /**
     * 通过下标获取对应Ident的值
     * 
     * @param varSymbol
     * @param index
     * @return
     */
    public static int getVarValueofIndex(VarSymbol varSymbol, int index) {
        int value = varSymbol.value.get(index);
        return value;
    }

    /**
     * 查找对应函数符号表
     * 
     * @param funcSymTabID
     * @param ident
     * @return
     */
    public static SymTab findFuncSymTab(int funcSymTabID) {
        SymTab funcSymTab = null;

        for (SymTab symTab : global_symTab.childSymTabs) {
            if (symTab.id == funcSymTabID) {
                funcSymTab = symTab;
                break;
            }
        }

        return funcSymTab;
    }

    /**
     * 在符号表中查找函数符号
     * 
     * @param ident
     * @return
     */
    public static FuncSymbol findFuncSymbolfromSymTab(String ident) {
        Symbol symbol = global_symTab.curSymTab.get(ident);
        FuncSymbol funcSymbol = (FuncSymbol) symbol;

        return funcSymbol;
    }

    public static int calExpValue(ArrayList<Token> exp) {
        Stack<Character> signs = new Stack<>();
        Stack<Integer> num = new Stack<>();

        int temp = 0;
        Character sign = ' ';
        boolean needMinus = false;

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON") || t.tk.equals("CHARCON")) { // 常量
                temp = Integer.valueOf(t.str);
                if (needMinus) {
                    temp = -temp;
                    needMinus = false;
                }
                num.push(temp);
                sign = ' ';
            }

            else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%")) { // 运算符
                if (t.str.charAt(0) == '-' && sign != ' ') {
                    needMinus = true;
                }
                sign = t.str.charAt(0);
                signs.push(sign);
                // 非运算符需要在结尾将sign置为' '
            }

            else if (t.tk.equals("IDENFR")) { // 标识符
                if (i < exp.size() && Tools.GetCountTK(i + 1).str != "[") {
                    temp = getVarValueofIndex(findVarfromSymTab(t.str), 0);
                    if (needMinus) {
                        temp = -temp;
                        needMinus = false;
                    }
                    num.push(temp);
                } else if (i < exp.size() && Tools.GetCountTK(i + 1).str == "[") {
                    int begin = i + 2;
                    int level = 1;
                    for (int j = begin; j < exp.size(); j++) {
                        if (exp.get(j).str.equals("[")) {
                            level++;
                        } else if (exp.get(j).str.equals("]")) {
                            level--;
                        }

                        if (level == 0) {
                            i = j;
                            break;
                        }
                    }
                    int end = i - 1;

                    int index = calExpValue(Tools.GetExpfromIndex(begin, end));
                    temp = getVarValueofIndex(findVarfromSymTab(t.str), index);
                    if (needMinus) {
                        temp = -temp;
                        needMinus = false;
                    }
                    num.push(temp);
                    sign = ' ';
                } else if (i < exp.size() && Tools.GetCountTK(i + 1).str == "(") {
                    // FIXME: 调用函数
                }
            }

            else if (t.str.equals("(")) { // 左括号
                i++;
                int j = i;
                int level = 1;

                while (j < exp.size()) {
                    if (exp.get(j).str.equals("(")) {
                        level++;
                    } else if (exp.get(j).str.equals(")")) {
                        level--;
                    }

                    if (level == 0) {
                        break;
                    }

                    j++;
                }

                temp = calExpValue(Tools.GetExpfromIndex(i, j - 1));
                if (needMinus) {
                    temp = -temp;
                    needMinus = false;
                }
                num.push(temp);
                i = j - 1;
                sign = ' ';
            }
        }

        // 检查表达式栈是否能够进行计算
        int result = optimizeExpression(num, signs);
        // FIXME: 优化表达式

        return result;
    }

    public static int optimizeExpression(Stack<Integer> num, Stack<Character> signs) {
        Stack<Integer> tempNum = new Stack<>();
        Stack<Character> tempSigns = new Stack<>();

        // First pass: handle *, /, %
        while (!signs.isEmpty()) {
            char sign = signs.pop();
            int right = num.pop();
            int left = num.pop();

            if (sign == '*' || sign == '/' || sign == '%') {
                int result = 0;
                switch (sign) {
                    case '*':
                        result = left * right;
                        break;
                    case '/':
                        result = left / right;
                        break;
                    case '%':
                        result = left % right;
                        break;
                }
                num.push(result); // Push the result back to num stack
            } else {
                // If not *, /, %, save in temp stacks for later
                num.push(left);
                tempNum.push(right); // Save top of num stack to tempNum
                tempSigns.push(sign);
            }
        }

        // Move remaining numbers and signs back to original stacks
        while (!tempNum.isEmpty()) {
            num.push(tempNum.pop());
        }
        while (!tempSigns.isEmpty()) {
            signs.push(tempSigns.pop());
        }

        // Second pass: handle + and -
        while (!signs.isEmpty()) {
            char sign = signs.pop();
            int right = num.pop();
            int left = num.pop();

            int result = 0;
            switch (sign) {
                case '+':
                    result = left + right;
                    break;
                case '-':
                    result = left - right;
                    break;
            }
            num.push(result);
        }

        // Final result will be the only number left in the num stack
        return num.pop();
    }
}
