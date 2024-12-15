package Frontend.Syntax.Children;

import SymbolTable.VarSymbol.VarTypes;
import SymbolTable.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

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
     * @param vp
     */
    public static void AddVarSymbol(boolean isConst, String btype, VarPart vp) { // wait for add more parameter
        if (!utils.GetRepeat()) {
            int size = 0;

            if (vp.isArray) {// 处理ConstExp
                if (vp.sizeExp == null) { // 函数传递的参数
                    size = -1;
                } else {
                    size = calConstExp(vp.sizeExp);
                }

            }

            if (isConst) {
                if (btype.equals("Int")) {
                    if (vp.isArray) {
                        utils.addVarSymbol(vp.name, VarTypes.ConstIntArray, size, vp.offset);
                    } else {
                        utils.addVarSymbol(vp.name, VarTypes.ConstInt, size, vp.offset);
                    }
                } else {
                    if (vp.isArray) {
                        utils.addVarSymbol(vp.name, VarTypes.ConstCharArray, size, vp.offset);
                    } else {
                        utils.addVarSymbol(vp.name, VarTypes.ConstChar, size, vp.offset);
                    }
                }
            } else {
                if (btype.equals("Int")) {
                    if (vp.isArray) {
                        utils.addVarSymbol(vp.name, VarTypes.IntArray, size, vp.offset);
                    } else {
                        utils.addVarSymbol(vp.name, VarTypes.Int, size, vp.offset);
                    }
                } else {
                    if (vp.isArray) {
                        utils.addVarSymbol(vp.name, VarTypes.CharArray, size, vp.offset);
                    } else {
                        utils.addVarSymbol(vp.name, VarTypes.Char, size, vp.offset);
                    }
                }
            }
        }
    }

    public static void AddFuncSymbol(FuncPart fp, boolean isFuncRepeat) { // wait for add more parameter
        if (!isFuncRepeat) {
            utils.addFuncSymbol(fp.name, fp.returnType, fp.paramTypes, fp.paramNumber); // FIXME: fp参数修改
        }
    }

    public static int calConstExp(ArrayList<Token> exp) {
        Deque<Integer> nums = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();

        Boolean needNegative = false;
        int temp = 0;
        Character op = ' ';

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON")) {
                temp = NegativeTool(needNegative, Integer.valueOf(t.str));
                nums.addLast(temp);

                op = ' ';
                needNegative = false;
            } else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%")) {

                if (op != ' ' && t.str.equals("-")) { // 处理UnaryOP -
                    needNegative = !needNegative;
                } else if (op != ' ' && t.str.equals("+")) { // 处理UnaryOP +
                    continue;
                } else {
                    op = t.str.charAt(0);
                    ops.addLast(op);
                }
            } else if (t.str.equals("(")) {
                int begin = i + 1, j = i + 1;
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

                ArrayList<Token> subExp = GetSubExpfromIndex(begin, j - 1, exp);
                i = j;

                temp = calConstExp(subExp);
                temp = NegativeTool(needNegative, temp);
                nums.addLast(temp);

                op = ' ';
                needNegative = false;
            }
        }

        Deque<Integer> nums2 = new ArrayDeque<>();
        Deque<Character> ops2 = new ArrayDeque<>();

        while (!ops.isEmpty()) {
            op = ops.removeFirst();
            if (op == '+' || op == '-') {
                nums2.addLast(nums.removeFirst());
                ops2.addLast(op);
            } else {
                int left = nums.removeFirst();
                int right = nums.removeFirst();
                int ret = processCal(left, right, op);
                nums.addFirst(ret);
            }
        }
        nums2.addLast(nums.removeFirst());

        int left = nums2.removeFirst();
        while (!ops2.isEmpty()) {
            int right = nums2.removeFirst();
            op = ops2.removeFirst();
            left = processCal(left, right, op);
        }

        return left;
    }

    public static Integer NegativeTool(Boolean needNegative, Integer num) {
        if (needNegative) {
            return -num;
        }
        return num;
    }

    public static Integer processCal(int left, int right, Character op) {
        if (op == '+') {
            return left + right;
        } else if (op == '-') {
            return left - right;
        } else if (op == '*') {
            return left * right;
        } else if (op == '/') {
            return left / right;
        } else if (op == '%') {
            return left % right;
        } else {
            throw new RuntimeException("Invalid operator");
        }
    }

    /**
     * 通过下标获取当前EXP的子Exp
     * 
     * @param begin
     * @param end
     * @return
     */
    public static ArrayList<Token> GetSubExpfromIndex(int start, int end, ArrayList<Token> exp) {
        ArrayList<Token> ret = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            ret.add(exp.get(i));
        }
        return ret;
    }
}
