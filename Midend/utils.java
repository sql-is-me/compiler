import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import Frontend.Pair;
import Frontend.Lexer.Lexer.Token;
import SymbolTable.FuncSymbol;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.FuncSymbol.FuncTypes;
import Operands.ConstOp;
import Operands.FuncOp;
import Operands.Operands;
import Operands.RegOp;
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

    public static Operands calExp(ArrayList<Token> exp, boolean isGlobalInit) {
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
                    Operands temp;
                    if (exp.get(i + 1).str.equals("[")) { // 数组位置处理
                        i += 2;// [

                        int level = 1;
                        int begin = i;
                        while (level != 0) {
                            if (exp.get(i).tk.equals("[")) {
                                level++;
                            } else if (exp.get(i).str.equals("]")) {
                                level--;
                                if (level == 0) {
                                    break;
                                }
                            }
                            i++;
                        }
                        ArrayList<Token> posExp = GetSubExpfromIndex(begin, i - 1, exp);
                        Operands tempOp = calExp(posExp, isGlobalInit);
                        temp = new VarOp(varSymbol, tempOp, needNegative);
                    } else { // 变量处理，pos返回 -1 ConstOp即可
                        temp = new VarOp(varSymbol, new ConstOp(-1, false), needNegative);
                    }

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
                    ArrayList<Operands> paramsOps = new ArrayList<>();
                    for (ArrayList<Token> param : params) {
                        Operands paramOp = calExp(param, isGlobalInit);
                        paramsOps.add(paramOp);
                    }

                    Operands temp = new FuncOp(funcSymbol, paramsOps, needNegative);
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

                Operands temp = calExp(subExp, isGlobalInit);
                operands.addLast(temp);

                op = ' ';
                needNegative = false;
            }
        }

        return processCal(operands, ops, isGlobalInit);
    }

    public static Operands processCal(Deque<Operands> operands, Deque<Character> ops, boolean isGlobalInit) {
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
                if (left instanceof VarOp) {
                    left = handleVarOpInExp((VarOp) left);
                } else if (left instanceof FuncOp) {
                    left = handleFuncOpInExp((FuncOp) left);
                }
                Operands right = operands.removeFirst();
                if (right instanceof VarOp) {
                    right = handleVarOpInExp((VarOp) right);
                } else if (right instanceof FuncOp) {
                    right = handleFuncOpInExp((FuncOp) right);
                }

                Operands ret = readyforTransExpCode(left, right, op);
                operands.addFirst(ret);
            }
        }
        operands2.addLast(operands.removeFirst());

        Operands left = operands2.removeFirst();
        if (left instanceof VarOp) {
            left = handleVarOpInExp((VarOp) left);
        } else if (left instanceof FuncOp) {
            left = handleFuncOpInExp((FuncOp) left);
        }

        while (!ops2.isEmpty()) {
            Operands right = operands2.removeFirst();
            if (right instanceof VarOp) {
                right = handleVarOpInExp((VarOp) right);
            } else if (right instanceof FuncOp) {
                right = handleFuncOpInExp((FuncOp) right);
            }
            op = ops2.removeFirst();
            left = readyforTransExpCode(left, right, op);
        }

        if (left.needNegative) {// 处理负号
            if (left instanceof RegOp) {
                ((RegOp) left).regNo = CodeGenerater.CreatNegativeCode(((RegOp) left).regNo);
                left.needNegative = false;
            } else if (left instanceof ConstOp) {
                ((ConstOp) left).value = -((ConstOp) left).value;
                left.needNegative = false;
            } else {
                throw new RuntimeException("Unknow Operands when 处理负号");
            }
        }

        return left;
    }

    public static Operands readyforTransExpCode(Operands left, Operands right, Character op) {
        if (left instanceof ConstOp) {
            Integer left_c;
            if (left.needNegative) { // 处理负号
                left_c = -((ConstOp) left).value;
            } else {
                left_c = ((ConstOp) left).value;
            }

            if (right instanceof ConstOp) { // 双常值直接计算返回即可
                Integer right_c;
                if (right.needNegative) { // 处理负号
                    right_c = ((ConstOp) right).value;
                } else {
                    right_c = ((ConstOp) right).value;
                }

                return new ConstOp(ConstCal(left_c, right_c, op), false);
            } else if (right instanceof RegOp) { // 常值与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.needNegative) { // 处理负号
                    rightRegOp.regNo = CodeGenerater.CreatNegativeCode(rightRegOp.regNo);
                    rightRegOp.needNegative = false;
                }

                if (rightRegOp.type == 8) { // 类型转换i32
                    CodeGenerater.CreatTransTypeCode(rightRegOp);
                    rightRegOp = new RegOp(rightRegOp.regNo, 32, rightRegOp.isArray, rightRegOp.needNegative);
                }

                Integer retReg = CodeGenerater.CreatCalExp(true, left_c, false, rightRegOp.regNo, op);
                return new RegOp(retReg, rightRegOp.type, rightRegOp.isArray, rightRegOp.needNegative);
            }
        } else if (left instanceof RegOp) {
            RegOp leftRegOp = (RegOp) left;

            if (leftRegOp.needNegative) { // 处理负号
                leftRegOp.regNo = CodeGenerater.CreatNegativeCode(leftRegOp.regNo);
                leftRegOp.needNegative = false;
            }

            if (leftRegOp.type == 8) { // 类型转换i32
                CodeGenerater.CreatTransTypeCode(leftRegOp);
                leftRegOp = new RegOp(leftRegOp.regNo, 32, leftRegOp.isArray, leftRegOp.needNegative);
            }

            if (right instanceof ConstOp) { // 常值与RegOp计算，返回RegOp
                Integer right_c;
                if (right.needNegative) { // 处理负号
                    right_c = ((ConstOp) right).value;
                } else {
                    right_c = ((ConstOp) right).value;
                }

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, true, right_c, op);
                return new RegOp(retReg, leftRegOp.type, leftRegOp.isArray, leftRegOp.needNegative);
            } else if (right instanceof RegOp) { // RegOp与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.needNegative) { // 处理负号
                    rightRegOp.regNo = CodeGenerater.CreatNegativeCode(rightRegOp.regNo);
                    rightRegOp.needNegative = false;
                }

                if (rightRegOp.type == 8) { // 类型转换i32
                    CodeGenerater.CreatTransTypeCode(rightRegOp);
                    rightRegOp = new RegOp(rightRegOp.regNo, 32, rightRegOp.isArray, rightRegOp.needNegative);
                }

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, false, rightRegOp.regNo, op);
                return new RegOp(retReg, 32, false, false);
            }
        }
        throw new RuntimeException("Invalid operator in readyforTransExpCode");
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

    /**
     * 处理VarOp，返回ConstOp或RegOp
     * 
     * @param varOp
     * @return ConstOp或RegOp
     */
    public static Operands handleVarOpInExp(VarOp varOp) {
        int pos;
        int posReg = 0;
        if (varOp.pos instanceof ConstOp) {
            pos = ((ConstOp) varOp.pos).value;
        } else {
            pos = -2; // 证明pos是一RegOp
            posReg = ((RegOp) varOp.pos).regNo;
        }
        Register reg = regMap.get(varOp.varSymbol);
        if (reg == null) {
            throw new RuntimeException("未插入寄存器表");
        }

        Pair ret = reg.getReg(pos, posReg);
        if ((Boolean) ret.a.equals(true)) { // 拿到的是一个常值
            return new ConstOp((Integer) ret.b, varOp.needNegative);
        } else { // 拿到了值寄存器
            return new RegOp((Integer) ret.b, varOp.type, varOp.isArray, varOp.needNegative);
        }
    }

    /**
     * 处理FuncOp，返回RegOp
     *
     * @param funcOp
     * @return RegOp
     */
    public static Operands handleFuncOpInExp(FuncOp funcOp) {
        Integer retReg = CodeGenerater.CreatCallFunc(funcOp.funcSymbol, funcOp.params);
        if (funcOp.funcSymbol.returnType.equals(FuncTypes.IntFunc)) {
            return new RegOp(retReg, 32, false, funcOp.needNegative);
        } else if (funcOp.funcSymbol.returnType.equals(FuncTypes.CharFunc)) {
            return new RegOp(retReg, 8, false, funcOp.needNegative);
        } else { // void
            return null;
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
        // Register reg;
        // if (varSymbol.size == 1) {
        // reg = new Register(varSymbol.size, false);
        // } else {
        // reg = new Register(varSymbol.size, true);
        // reg.allocPointReg(CodeGenerater.CreatAllocCode(regNum_Record, regNum));
        // }
        // regMap.put(varSymbol, reg);
    }

}
