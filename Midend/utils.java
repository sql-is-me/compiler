import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import Frontend.Lexer.Lexer.Token;
import SymbolTable.FuncSymbol;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import Operands.ConstOp;
import Operands.FuncOp;
import Operands.Operands;
import Operands.VarOp;

public class utils {

    public static Symbol findSymbol(String name) {
        SymTab symTab = IterateTK.cur_symTab;

        while (symTab != null) {
            if (symTab.curSymTab.containsKey(name)) {
                return symTab.curSymTab.get(name);
            }
            symTab = symTab.lastSymTab;
        }

        throw new RuntimeException("Symbol " + name + " not found");
    }

    public static Operands calExp(ArrayList<Token> exp) {
        Deque<Operands> operands = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();

        Boolean needNegative = false;
        Character op = ' ';

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON") || t.tk.equals("CHRCON")) { // 常量处理
                Operands temp = new ConstOp(Integer.parseInt(t.str), needNegative);
                operands.addLast(temp);

                op = ' ';
                needNegative = false;
            } else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%")) { // 表达式符号处理

                if (op != ' ' && t.str.equals("-")) { // 处理UnaryOP -
                    needNegative = !needNegative;
                } else if (op != ' ' && t.str.equals("+")) { // 处理UnaryOP +
                    continue;
                } else {
                    op = t.str.charAt(0);
                    ops.addLast(op);
                }
            } else if (t.tk.equals("IDENFR")) { // 变量处理
                Symbol symbol = findSymbol(t.str);

                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    Operands temp = new VarOp(varSymbol, needNegative);
                    operands.addLast(temp);
                } else {
                    FuncSymbol funcSymbol = (FuncSymbol) symbol;

                    i += 2; // indentifier (
                    int begin = i;
                    for (int level = 1; i < exp.size(); i++) {
                        if (exp.get(i).tk.equals("(")) {
                            level++;
                        } else if (exp.get(i).str.equals(")")) {
                            level--;
                        }

                        if (level == 0) {
                            break;
                        }
                    }

                    ArrayList<Token> subExp = GetSubExpfromIndex(begin, i - 1, exp);
                    ArrayList<ArrayList<Token>> params = getFuncParams(funcSymbol.paramNumber, subExp);
                    Operands temp = new FuncOp(funcSymbol, params, needNegative);
                    operands.addLast(temp);
                }

                op = ' ';
                needNegative = false;
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

                Operands temp = calExp(subExp);
                operands.addLast(temp);

                op = ' ';
                needNegative = false;
            }
        }

        return processCal(operands, ops);
    }

    public static Operands processCal(Deque<Operands> operands, Deque<Character> ops) {
        Deque<Operands> operands2 = new ArrayDeque<>();
        Deque<Character> ops2 = new ArrayDeque<>();
        Character op = ' ';

        while (!ops.isEmpty()) {
            op = ops.removeFirst();
            if (op == '+' || op == '-') {
                operands2.addLast(operands.removeFirst());
                ops2.addLast(op);
            } else {
                Operands left = operands.removeFirst();
                Operands right = operands.removeFirst();
                Operands ret = readyforTransExpCode(left, right, op);
                operands.addFirst(ret);
            }
        }
        operands2.addLast(operands.removeFirst());

        Operands left = operands2.removeFirst();
        while (!ops2.isEmpty()) {
            Operands right = operands2.removeFirst();
            op = ops2.removeFirst();
            left = readyforTransExpCode(left, right, op);
        }

        return left;
    }

    public static Operands readyforTransExpCode(Operands left, Operands right, Character op) {
        if (left instanceof ConstOp && right instanceof ConstOp) { // 常值直接计算返回即可
            int left_c, right_c;
            if (left.needNegative) {
                left_c = -((ConstOp) left).value;
            } else {
                left_c = ((ConstOp) left).value;
            }
            if (right.needNegative) {
                right_c = ((ConstOp) right).value;
            } else {
                right_c = ((ConstOp) right).value;
            }

            return new ConstOp(ConstCal(left_c, right_c, op), false);
        }

        // TODO:
        // 处理VarOp,FuncOp,ConstOp,RegOp的混合计算，生成相应中间代码并生成对应Operands对象(RegOp或者ConstOp)压回队列

        return null;
    }

    public static Integer ConstCal(int left, int right, Character op) {
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

    public static Integer handleVarOpInExp(Operands VarOp) { // TODO:处理VarOp

        return 0;
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

    /**
     * 通过下标获取EXP
     * 
     * @param begin
     * @param end
     * @return
     */
    public static ArrayList<Token> GetExpfromIndex(int begin, int end) {
        ArrayList<Token> al = new ArrayList<>();
        while (begin <= end) {
            al.add(IterateTK.token.get(begin));
            begin++;
        }
        return al;
    }

    public static ArrayList<ArrayList<Token>> getFuncParams(int paramNum, ArrayList<Token> subexp) {
        ArrayList<ArrayList<Token>> params = new ArrayList<>();
        int begin = 0, count = 0;

        for (int i = 0; i < subexp.size() && count < paramNum - 1; i++) { // 前面paramNum-1个参数
            if (subexp.get(i).tk.equals(",")) {
                count++;
                params.add(GetSubExpfromIndex(begin, i - 1, subexp));
                begin = i + 1;
            }
        }

        if (count == paramNum - 1) { // 最后一个参数
            params.add(GetSubExpfromIndex(begin, subexp.size() - 1, subexp));
        }

        return params;
    }

    /* ————————————————————————————————————————————————————————————————————— */

    /** 寄存器图 */
    public static Map<VarSymbol, Register> regMap = new HashMap<>();

    /** 记录寄存器No */
    public static Integer regNum = 0;
    /** 记录在进入函数体时的全局寄存器No */
    public static Integer regNum_Record;

    public static void enterFuncBody() {
        regNum_Record = regNum;
    }

    public static void quitFuncBody() {
        regNum = regNum_Record;
    }

    /**
     * 获取寄存器编号,并将寄存器编号加1
     * 
     * @return 返回寄存器编号
     */
    public static Integer getRegNum() {
        return regNum++;
    }

    public static void addSymboltoRegMap(VarSymbol varSymbol) { // TODO:向寄存器表中添加对应符号寄存器，需初始化分配栈
        Register reg;
        if (varSymbol.size == 1) {
            reg = new Register(varSymbol.size, false);
        } else {
            reg = new Register(varSymbol.size, true);
            reg.allocPointReg(CodeGenerater.CreatAllocCode(regNum_Record, regNum));
        }
        regMap.put(varSymbol, reg);
    }

}
