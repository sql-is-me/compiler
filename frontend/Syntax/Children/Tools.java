package Frontend.Syntax.Children;

import SymbolTable.VarSymbol.VarTypes;
import SymbolTable.utils;

import java.util.ArrayList;
import java.util.Stack;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Node;
import Frontend.Syntax.Syntax;
import Frontend.Syntax.Syntax.NodeType;

public class Tools {
    public static Token GetCountTK(int count) {
        return CompUnit.words.get(count);
    }

    public static Token GetNowTK() {
        return CompUnit.words.get(CompUnit.count);
    }

    public static Token LookNextTK() { // 看下一个TK
        return CompUnit.words.get(CompUnit.count + 1);
    }

    public static void WriteLine(NodeType type, int index) {
        Node node = new Node(type, index);
        Syntax.getNodes().add(node);
    }

    public static ArrayList<Token> GetExpfromIndex(int begin, int end) {
        ArrayList<Token> al = new ArrayList<>();
        while (begin <= end) {
            al.add(CompUnit.words.get(begin));
            begin++;
        }
        return al;
    }

    /**
     * 通过调用utils来向符号表添加符号
     * 
     * @param isConst
     * @param btype
     * @param va
     */
    public static void AddVarSymbol(boolean isConst, String btype, VarsAttribute va) {
        if (!utils.GetRepeat()) {
            if (isConst) {
                if (btype.equals("Int")) {
                    if (va.isArray) {
                        utils.addVarSymbol(va.name, VarTypes.ConstIntArray, va.arrSize, va.initValues, va.valueExp);
                    } else {
                        utils.addVarSymbol(va.name, VarTypes.ConstInt, va.arrSize, va.initValues, va.valueExp);
                    }
                } else {
                    if (va.isArray) {
                        utils.addVarSymbol(va.name, VarTypes.ConstCharArray, va.arrSize, va.initValues, va.valueExp);
                    } else {
                        utils.addVarSymbol(va.name, VarTypes.ConstChar, va.arrSize, va.initValues, va.valueExp);
                    }
                }
            } else {
                if (btype.equals("Int")) {
                    if (va.isArray) {
                        utils.addVarSymbol(va.name, VarTypes.IntArray, va.arrSize, va.initValues, va.valueExp);
                    } else {
                        utils.addVarSymbol(va.name, VarTypes.Int, va.arrSize, va.initValues, va.valueExp);
                    }
                } else {
                    if (va.isArray) {
                        utils.addVarSymbol(va.name, VarTypes.CharArray, va.arrSize, va.initValues, va.valueExp);
                    } else {
                        utils.addVarSymbol(va.name, VarTypes.Char, va.arrSize, va.initValues, va.valueExp);
                    }
                }
            }
        }
    }

    public static void AddFuncSymbol(FuncPart fp, boolean isFuncRepeat) { // wait for add more parameter
        if (!isFuncRepeat) {
            utils.addFuncSymbol(fp.name, fp.returnType, fp.paramTypes, fp.paramNumber);
        }
    }

    public static int calConstExp(ArrayList<Token> exp) {
        Stack<Integer> num = new Stack<>();
        int temp = 0;
        int result = 0;
        char sign = '+';
        boolean needCal = false;
        boolean needMinus = false;

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON")) {
                temp = Integer.valueOf(t.str);

                if (needMinus) {
                    num.push(-temp);
                    needMinus = false;
                } else if (needCal) {
                    int num1 = num.pop();
                    if (sign == '*') {
                        num.push(num1 * temp);
                    } else if (sign == '/') {
                        num.push(num1 / temp);
                    } else if (sign == '%') {
                        num.push(num1 % temp);
                    }
                    needCal = false;
                } else {
                    num.push(temp);
                }

            } else if (t.str.equals("(")) {
                i++;
                int j = i;
                int level = 1;

                while (j < exp.size()) {
                    if (exp.get(j).str.equals("(")) {
                        level++;
                    } else if (exp.get(j).str.equals(")")) {
                        level--;
                    }

                    j++;

                    if (level == 0) {
                        break;
                    }
                }
                ArrayList<Token> subExp = GetExpfromIndex(i, j - 2); // FIXME: 有没有可能这里会出现()的情况？

                temp = calConstExp(subExp);
                if (needMinus) {
                    num.push(-temp);
                    needMinus = false;
                } else if (needCal) {
                    int num1 = num.pop();
                    if (sign == '*') {
                        num.push(num1 * temp);
                    } else if (sign == '/') {
                        num.push(num1 / temp);
                    } else if (sign == '%') {
                        num.push(num1 % temp);
                    }
                    needCal = false;
                } else {
                    num.push(temp);
                }

                i = j - 1;
            }

            else {
                sign = t.str.charAt(0);
                if (sign == '-') {
                    needMinus = true;
                } else if (sign == '*' || sign == '/' || sign == '%') {
                    needCal = true;
                }
            }

        }
        for (int val : num) {
            result += val;
        }

        return result;
    }
}
