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
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class M_utils {

    public static void findFuncPosinTokens(FuncSymbol funcSymbol) { // 直到找到函数的{
        MidCodeGenerate.pos = funcSymbol.offset;
        while (true) {
            if (Tools.GetCountTK(MidCodeGenerate.pos).str.equals("{")) {
                break;
            }
            MidCodeGenerate.pos++;
        }
    }

    /** 找到本句尾 */
    public static void findEndofScope() {
        for (Token t = Tools.GetCountTK(MidCodeGenerate.pos); !t.str
                .equals(";"); t = Tools.GetCountTK(MidCodeGenerate.pos)) {
            MidCodeGenerate.pos++;
        }
    }

    public static String DeclareString() { // TODO: 声明字符串常量
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    /**
     * 计算多个表达式的值
     *
     * @param exp
     * @return
     */
    public static ArrayList<Integer> calExpsValue(VarSymbol varSymbol, ArrayList<ArrayList<Token>> valueExp) {
        ArrayList<Integer> values = new ArrayList<>();
        Operands temp = null;

        for (ArrayList<Token> exp : valueExp) {
            if (varSymbol.type.equals(VarTypes.ConstInt) || varSymbol.type.equals(VarTypes.ConstChar)) {
                temp = calExpValue(exp);
            }
            temp = calExpValue(exp);
            if (temp.kind == 0) {
                values.add(temp.value);
            } else if (temp.kind == 2) {
                varSymbol.needAssignVReg = true;
                values.add(0);
            }
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

        if (MidCodeGenerate.cur_symTab.curSymTab.containsKey(ident)) {
            symbol = MidCodeGenerate.cur_symTab.curSymTab.get(ident);
        } else {
            SymTab tempSymTab = MidCodeGenerate.cur_symTab;
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

        for (SymTab symTab : MidCodeGenerate.global_symTab.childSymTabs) {
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
        Symbol symbol = MidCodeGenerate.global_symTab.curSymTab.get(ident);
        FuncSymbol funcSymbol = (FuncSymbol) symbol;

        return funcSymbol;
    }

    public static class Operands {
        int kind; // 0:const 1:var 2:子表达式
        int type; // 0:int 1:char
        int value;
        boolean isdetermind; // 是否能够通过value来获取对应值
        boolean needMinus;
        VarSymbol varSymbol;
        int retRegNO; // 仅子表达式可用

        public Operands() {
            varSymbol = null;
        }
    }

    public static Operands calExpValue(ArrayList<Token> exp) {
        Deque<Operands> operands = new ArrayDeque<>();
        Deque<String> operators = new ArrayDeque<>();
        Boolean needMinus = false;
        String op = "+";

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);
            Operands temp = new Operands();

            if (t.tk.equals("INTCON") || t.tk.equals("CHARCON")) { // 常量
                temp.kind = 0;
                temp.type = 0; // int
                temp.value = Integer.valueOf(t.str);
                temp.isdetermind = true;
                if (needMinus) {
                    temp.needMinus = true;
                    needMinus = false;
                } else {
                    temp.needMinus = false;
                }

                operands.addLast(temp);

                op = " ";
            }

            else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%")) { // 运算符
                op = t.str;
                if (op == "-" && op != " ") {
                    needMinus = true;
                } else {
                    operators.addLast(op);
                }

                // 非运算符需要在结尾将op置为" "
                // 并初始化temp.needMinus为false
            }

            else if (t.tk.equals("IDENFR")) { // 标识符
                temp.kind = 1; // var
                if (i < exp.size() && (Tools.GetCountTK(i + 1).str != "[" || Tools.GetCountTK(i + 1).str != "(")) { // 常变量
                    VarSymbol varSymbol = findVarfromSymTab(t.str);
                    temp.varSymbol = varSymbol;

                    if (varSymbol.valueisDetermined.get(0) == true) {
                        temp.isdetermind = true;
                    } else {
                        temp.isdetermind = false;
                    }

                    if (varSymbol.type.equals(VarTypes.Int) || varSymbol.type.equals(VarTypes.ConstInt)) {
                        temp.type = 0;// int
                    } else if (varSymbol.type.equals(VarTypes.Char) || varSymbol.type.equals(VarTypes.ConstChar)) {
                        temp.type = 1; // char
                    }

                    if (needMinus) {
                        temp.needMinus = true;
                        needMinus = false;
                    } else {
                        temp.needMinus = false;
                    }

                    operands.addLast(temp);

                    op = " ";
                } else if (i < exp.size() && Tools.GetCountTK(i + 1).str == "[") { // 数组
                    // int begin = i + 2;
                    // int level = 1;
                    // for (int j = begin; j < exp.size(); j++) {
                    // if (exp.get(j).str.equals("[")) {
                    // level++;
                    // } else if (exp.get(j).str.equals("]")) {
                    // level--;
                    // }

                    // if (level == 0) {
                    // i = j;
                    // break;
                    // }
                    // }
                    // int end = i - 1;

                    // int index = calExpValue(Tools.GetExpfromIndex(begin, end));
                    // temp = getVarValueofIndex(findVarfromSymTab(t.str), index);
                    // if (needMinus) {
                    // temp = -temp;
                    // needMinus = false;
                    // }
                    // num.push(temp);
                    // sign = ' ';
                } else if (i < exp.size() && Tools.GetCountTK(i + 1).str == "(") {
                    FuncSymbol funcSymbol = findFuncSymbolfromSymTab(t.str);
                    M_utils.FuncRParams funcRParams = new M_utils.FuncRParams();
                    funcRParams.size = funcSymbol.paramNumber;

                    MidCodeGenerate.pos += 2;// ( + 1
                    temp.retRegNO = MidCodeGenerate.callFunc(funcSymbol, funcRParams);
                    if (funcSymbol.returnType == FuncTypes.IntFunc) {
                        temp.type = 0;
                    } else { // char
                        temp.type = 1;
                    }
                    temp.needMinus = false;
                    temp.isdetermind = true;
                    temp.kind = 2; // 子表达式
                }
            }

            else if (t.str.equals("(")) { // 左括号
                // temp.kind = 2; // 子表达式
                int begin = i + 1;
                int end = begin;
                for (int j = i + 1, level = 1; j < exp.size(); j++) { // 递归处理，获取对应位置
                    if (exp.get(j).str.equals("(")) {
                        level++;
                    } else if (exp.get(j).str.equals(")")) {
                        level--;
                    }

                    if (level == 0) {
                        end = j - 1;
                        i = j;
                        break;
                    }
                }

                temp = calExpValue(Tools.GetExpfromIndex(begin, end));
                if (needMinus) {
                    temp.needMinus = true;
                    needMinus = false;
                } else {
                    temp.needMinus = false;
                }

                operands.addLast(temp);

                op = " ";
            }
        }

        // 检查表达式栈是否能够进行计算优化
        return optimizeExpression(operands, operators);
    }

    /**
     * 表达式优化
     *
     * @param num
     * @param signs
     * @return
     */
    public static Operands optimizeExpression(Deque<Operands> operands, Deque<String> operations) {
        Deque<Operands> tempOperands = new ArrayDeque<>();
        Deque<String> tempOperations = new ArrayDeque<>();
        Operands left, right;
        String op;

        left = operands.pollFirst();
        while (!operations.isEmpty()) {
            op = operations.pollFirst();

            if (op == "+" || op == "-") {
                tempOperands.addLast(left);
                tempOperations.addLast(op);
                right = operands.pollFirst();
                left = right;
            } else {
                right = operands.pollFirst();

                // 如果左右操作数都已确定，进行计算
                if (left.isdetermind && right.isdetermind && (left.kind != 2 && right.kind != 2)) {
                    int result;
                    int right_value;
                    int left_value;

                    if (right.varSymbol == null) {
                        right_value = right.value;
                    } else {
                        right_value = right.value;
                    }
                    if (left.varSymbol == null) {
                        left_value = left.value;
                    } else {
                        left_value = left.value;
                    }

                    if (right.needMinus) {
                        right_value = -right_value;
                    }
                    if (left.needMinus) {
                        left_value = -left_value;
                    }
                    result = performOperation(left_value, right_value, op);

                    Operands resultOperand = new Operands(); // 计算结果并标记为已确定
                    resultOperand.needMinus = false;
                    resultOperand.isdetermind = true;
                    resultOperand.kind = 0; // 常量
                    resultOperand.type = 0; // int
                    resultOperand.value = result;

                    left = resultOperand; // 将计算结果交给left
                } else {
                    // 如果有未确定的操作数，执行代码生成
                    Operands resultOperand = new Operands();
                    resultOperand.retRegNO = generateExpCode(left, right, op);
                    resultOperand.needMinus = false;
                    resultOperand.isdetermind = true;
                    resultOperand.kind = 2; // 子表达式
                    resultOperand.type = 0; // int

                    left = resultOperand; // 将计算结果交给left
                }
            }
        }
        // 将最后一个操作数添加到临时队列
        tempOperands.addLast(left);

        left = tempOperands.pollFirst();
        // 处理剩余的未确定操作数
        while (!tempOperations.isEmpty()) {
            right = tempOperands.pollFirst();
            op = tempOperations.pollFirst();

            // 如果左右操作数都已确定，进行计算
            if (left.isdetermind && right.isdetermind && (left.kind != 2 && right.kind != 2)) {
                int result;
                int right_value;
                int left_value;

                if (right.varSymbol == null) {
                    right_value = right.value;
                } else {
                    right_value = right.value;
                }
                if (left.varSymbol == null) {
                    left_value = left.value;
                } else {
                    left_value = left.value;
                }

                if (right.needMinus) {
                    right_value = -right_value;
                }
                if (left.needMinus) {
                    left_value = -left_value;
                }
                result = performOperation(left_value, right_value, op);

                Operands resultOperand = new Operands(); // 计算结果并标记为已确定
                resultOperand.needMinus = false;
                resultOperand.isdetermind = true;
                resultOperand.kind = 2; // 子表达式
                resultOperand.type = 0; // int
                resultOperand.value = result;

                left = resultOperand; // 将计算结果交给left
            } else {
                // 如果有未确定的操作数，执行代码生成
                Operands resultOperand = new Operands();
                resultOperand.retRegNO = generateExpCode(left, right, op);
                resultOperand.needMinus = false;
                resultOperand.isdetermind = true;
                resultOperand.kind = 2; // 子表达式
                resultOperand.type = 0; // int

                left = resultOperand; // 将计算结果交给left
            }
        }

        return left;
    }

    // 执行运算
    private static int performOperation(int left, int right, String op) {
        switch (op) {
            case "*":
                return left * right;
            case "/":
                return left / right;
            case "%":
                return left % right;
            case "+":
                return left + right;
            case "-":
                return left - right;
            default:
                return 114514;
        }
    }

    public static int generateExpCode(Operands left, Operands right, String op) {
        StringBuilder sb = new StringBuilder();
        int retRegNO = MidCodeGenerate.regNO++;

        sb.append("% " + retRegNO + " = ");

        switch (op) {
            case "*":
                sb.append("mul ");
                break;

            case "/":
                sb.append("sdiv ");
                break;

            case "%":
                sb.append("srem ");
                break;

            case "+":
                sb.append("add ");
                break;

            case "-":
                sb.append("sub ");
                break;

            default:
                break;
        }

        if (left.isdetermind && !right.isdetermind) {
            if (right.varSymbol.needAssignVReg) {
                AssignValueRegister(right.varSymbol);
                generateLoadCode_assign(right.varSymbol);
            }

            if (left.kind == 0) { // const
                sb.append("i32 %" + right.varSymbol.valueRegID + ", " + left.value + "\n");
            } else if (left.kind == 1) { // var // FIXME: 数组,left还需要下标
                sb.append("i32 %" + right.varSymbol.valueRegID + ", " + left.varSymbol.value.get(0) + "\n");
            } else {
                sb.append("i32 %" + right.varSymbol.valueRegID + ", i32 %" + left.retRegNO + "\n");
            }

        } else if (right.isdetermind && !left.isdetermind) {
            if (left.varSymbol.needAssignVReg) {
                AssignValueRegister(left.varSymbol);
                generateLoadCode_assign(left.varSymbol);
            }

            if (right.kind == 0) { // const
                sb.append("i32 %" + left.varSymbol.valueRegID + ", " + right.value + "\n");
            } else if (right.kind == 1) { // var // FIXME: 数组,left还需要下标
                sb.append("i32 %" + left.varSymbol.valueRegID + ", " + right.varSymbol.value.get(0) + "\n");
            } else {
                sb.append("i32 %" + left.varSymbol.valueRegID + ", i32 %" + right.retRegNO + "\n");
            }

        } else {
            if (right.varSymbol.needAssignVReg) {
                AssignValueRegister(right.varSymbol);
                generateLoadCode_assign(right.varSymbol);
            }
            if (left.varSymbol.needAssignVReg) {
                AssignValueRegister(left.varSymbol);
                generateLoadCode_assign(left.varSymbol);
            }
            sb.append("i32 %" + left.varSymbol.valueRegID + ", " + right.varSymbol.valueRegID + "\n");
        }

        MidCodeGenerate.addLinetoAns(sb.toString());

        return retRegNO;
    }

    public static void updateVarSymbolValue(VarSymbol varSymbol, int value, int index, boolean isconst) {
        if (isconst) { // 仅仅在能够通过初始值计算出结果时才需要更改值
            varSymbol.value.set(index, value);
        }
        // 要不要设置为某个极限值？
        // 不需要，因为在找到值时，会判断needAssignVReg是否为true

        varSymbol.valueisDetermined.set(index, true);
        varSymbol.needAssignVReg = true;
    }

    public static void AssignValueRegister(VarSymbol varSymbol) {
        int regNO = MidCodeGenerate.regNO++;
        varSymbol.valueRegID = regNO;
        varSymbol.needAssignVReg = false;
    }

    /**
     * 变量赋值代码生成
     * 需要提前判断varSymbol.needAssignVReg是否为true
     * 如果为true，才需要加载
     * 
     * @param varSymbol
     */
    public static void generateLoadCode_assign(VarSymbol varSymbol) {
        StringBuilder sb = new StringBuilder();

        sb.append("%" + varSymbol.valueRegID + " = load ");
        if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
            sb.append("i32, i32* ");
        } else if ((varSymbol.type.equals(VarSymbol.VarTypes.Char)
                || varSymbol.type.equals(VarSymbol.VarTypes.ConstChar))) {
            sb.append("i8, i8* ");
        } else {
            // FIXME: 数组
        }
        sb.append("%" + varSymbol.stackRegID + "\n");

        MidCodeGenerate.addLinetoAns(sb.toString());
    }

    /**
     * 全局变量加载代码生成
     * 需要提前判断varSymbol.needAssignVReg是否为true
     * 如果为true，才需要加载
     * 
     * @param varSymbol
     */
    public static void generateLoadCode_global(VarSymbol varSymbol) {
        StringBuilder sb = new StringBuilder();
        sb.append("%" + varSymbol.valueRegID + " = load ");
        if (varSymbol.type.equals(VarSymbol.VarTypes.Int) || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
            sb.append("i32, i32* ");
        } else if (varSymbol.type.equals(VarSymbol.VarTypes.Char)) {
            sb.append("i8, i8* ");
        } else {
            // FIXME: 数组
        }
        sb.append("@" + varSymbol.name + "\n");

        MidCodeGenerate.addLinetoAns(sb.toString());
    }

    public static class FuncRParams {
        public int size;
        public ArrayList<Integer> type; // 0 int 1 char //FIXME:数组
        public ArrayList<Integer> value;
        public ArrayList<Boolean> isConst; // 如果是常量，直接取值
    }

    public static int generateCallFuncCode(FuncSymbol funcSymbol, FuncRParams funcRParams) {
        StringBuilder sb = new StringBuilder();
        int retRegNO;

        if (funcSymbol.returnType == FuncTypes.VoidFunc) {
            retRegNO = -1;
        } else {
            retRegNO = MidCodeGenerate.regNO++;
        }

        sb.append("%" + retRegNO + " = ");

        sb.append("call ");
        if (funcSymbol.returnType == FuncTypes.IntFunc) {
            sb.append("i32 ");
        } else if (funcSymbol.returnType == FuncTypes.CharFunc) {
            sb.append("i8 ");
        } else {
            sb.append("void ");
        }
        sb.append("@" + funcSymbol.name + "(");

        for (int i = 0; i < funcSymbol.paramNumber; i++) {
            if (funcRParams.type.get(i) == 0) {
                sb.append("i32 ");
            } else if (funcRParams.type.get(i) == 1) {
                sb.append("i8 ");
            }

            if (funcRParams.isConst.get(i)) {
                sb.append(funcRParams.value.get(i));
            } else {
                sb.append("%" + funcRParams.value.get(i));
            }

            if (i != funcSymbol.paramNumber - 1) {
                sb.append(", ");
            } else {
                sb.append(")");
            }
        }
        return retRegNO;
    }

}
