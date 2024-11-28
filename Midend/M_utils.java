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
    

    public static void findFuncPosinTokens() {
        // TODO: 查找token位置
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
    public static ArrayList<Integer> calExpsValue(ArrayList<ArrayList<Token>> valueExp) { // FIXME： 计算多个表达式的值，配置不确定状态
        ArrayList<Integer> values = new ArrayList<>();

        for (ArrayList<Token> exp : valueExp) {
            values.add(calExpValue(exp)); // FIXME : WARNING
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
        int type; // 0:const 1:var 2:子表达式
        int value;
        boolean isdetermind;
        boolean needMinus;
        int stackRegNO;
    }

    public static int calExpValue(ArrayList<Token> exp) {
        Deque<Operands> operands = new ArrayDeque<>();
        Deque<String> operators = new ArrayDeque<>();

        Operands temp = new Operands();
        String op = "+";

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON") || t.tk.equals("CHARCON")) { // 常量
                temp.type = 0;
                temp.isdetermind = true;
                temp.value = Integer.valueOf(t.str);
                operands.addLast(temp);

                op = " ";
                temp.needMinus = false;
            }

            else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%")) { // 运算符
                if (t.str.charAt(0) == '-' && op != " ") {
                    temp.needMinus = true;
                }
                op = t.str;
                operators.addLast(op);
                // 非运算符需要在结尾将op置为" "
                // 并初始化temp.needMinus为false
            }

            else if (t.tk.equals("IDENFR")) { // 标识符
                temp.type = 1; // var
                if (i < exp.size() && Tools.GetCountTK(i + 1).str != "[") { // 常变量
                    VarSymbol varSymbol = findVarfromSymTab(t.str);

                    if (varSymbol.valueisDetermined.get(0) == true) {
                        temp.value = getVarValueofIndex(findVarfromSymTab(t.str), 0);
                        temp.isdetermind = true;
                    } else {
                        temp.isdetermind = false;
                        temp.stackRegNO = varSymbol.stackRegID;
                    }

                    operands.addLast(temp);

                    op = " ";
                    temp.needMinus = false;
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
                    // FIXME: 调用函数
                }
            }

            else if (t.str.equals("(")) { // 左括号
                temp.type = 2; // 子表达式
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

                temp.stackRegNO = calExpValue(Tools.GetExpfromIndex(begin, end));
                temp.isdetermind = false;

                op = " ";
                temp.needMinus = false;
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
    public static int optimizeExpression(Deque<Operands> operands, Deque<String> operations) { // FIXME: 优化表达式
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
                if (left.isdetermind && right.isdetermind) {
                    int result;
                    if (right.needMinus) {
                        right.value = -right.value;
                    }
                    if (left.needMinus) {
                        left.value = -left.value;
                    }
                    result = performOperation(left.value, right.value, op);

                    Operands resultOperand = new Operands(); // 计算结果并标记为已确定
                    resultOperand.needMinus = false;
                    resultOperand.isdetermind = true;
                    resultOperand.type = 2; // 子表达式

                    left = resultOperand; // 将计算结果交给left
                } else {
                    // 如果有未确定的操作数，执行代码生成

                    tempOperands_mul.addLast(left);
                    left = right;
                    tempOperations_mul.addLast(op);
                }
            }
        }
        // 将最后一个操作数添加到临时队列
        tempOperands.addLast(left);

        // 处理剩余的未确定操作数
        while (!tempOperands.isEmpty()) {
            Operand left = tempOperands.pollFirst();
            Operand right = tempOperands.pollFirst();
            String op = tempOperations.pollFirst();

            // 此时 left 和 right 一定是未确定的操作数，无法直接计算
            // 你可以选择将其重新放回队列或者等待它们在后续阶段被填充
            operands.addFirst(left);
            operands.addFirst(right);
            operations.addFirst(op);
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

    // 执行运算
    private static int performOperation(int left, int right, String op) {
        switch (op) {
            case "*":
                return left * right;
            case "/":
                return left / right;
            case "%":
                return left % right;
            default:
                return 114514;
        }
    }
}
