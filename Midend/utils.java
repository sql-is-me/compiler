package Midend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

import Frontend.Pair;
import Frontend.Lexer.Lexer.Token;
import SymbolTable.FuncSymbol;
import SymbolTable.Register;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;
import Midend.Operands.ConstOp;
import Midend.Operands.FuncOp;
import Midend.Operands.Operands;
import Midend.Operands.RegOp;
import Midend.Operands.VarOp;

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
        Boolean needNot = false;
        Character op = ' ';

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON") || t.tk.equals("CHRCON")) { // 常量处理
                Operands temp = new ConstOp((int) t.str.charAt(0), needNegative, needNot);
                operands.addLast(temp);

                op = ' ';
                needNegative = false;
                needNot = false;
            } else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%") || t.str.equals("!")) { // 表达式符号处理

                if (op != ' ' && t.str.equals("-")) { // 处理UnaryOP -
                    needNegative = !needNegative;
                } else if (op != ' ' && t.str.equals("+")) { // 处理UnaryOP +
                    continue;
                } else if (op != ' ' && t.str.equals("!")) { // 处理UnaryOP +
                    needNot = !needNot;
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
                        temp = new VarOp(varSymbol, tempOp, needNegative, needNot);
                    } else { // 变量处理，pos返回 -1 ConstOp即可
                        temp = new VarOp(varSymbol, new ConstOp(-1, false, false), needNegative, needNot);
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

                    Operands temp = new FuncOp(funcSymbol, paramsOps, needNegative, needNot);
                    operands.addLast(temp);
                }

                op = ' ';
                needNegative = false;
                needNot = false;
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
                needNot = false;
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

            if (left.needNot) { // 处理!号
                if (((ConstOp) left).value != 0) {
                    left_c = 0;
                } else {
                    left_c = 1;
                }
            }

            if (right instanceof ConstOp) { // 双常值直接计算返回即可
                Integer right_c;
                if (right.needNegative) { // 处理负号
                    right_c = ((ConstOp) right).value;
                } else {
                    right_c = ((ConstOp) right).value;
                }

                if (right.needNot) { // 处理!号
                    if (((ConstOp) right).value != 0) {
                        right_c = 0;
                    } else {
                        right_c = 1;
                    }
                }

                return new ConstOp(ConstCal(left_c, right_c, op), false, false);
            } else if (right instanceof RegOp) { // 常值与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.needNegative) { // 处理负号
                    rightRegOp.regNo = CodeGenerater.CreatNegativeCode(rightRegOp.regNo);
                    rightRegOp.needNegative = false;
                } // FIXME 逻辑非

                if (rightRegOp.type == 8) { // 类型转换i32
                    rightRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(rightRegOp);
                }

                Integer retReg = CodeGenerater.CreatCalExp(true, left_c, false, rightRegOp.regNo, op);
                return new RegOp(retReg, rightRegOp.type, rightRegOp.isArray, rightRegOp.needNegative,
                        rightRegOp.needNot);
            }
        } else if (left instanceof RegOp) {
            RegOp leftRegOp = (RegOp) left;

            if (leftRegOp.needNegative) { // 处理负号
                leftRegOp.regNo = CodeGenerater.CreatNegativeCode(leftRegOp.regNo);
                leftRegOp.needNegative = false;
            }

            if (leftRegOp.type == 8) { // 类型转换i32
                leftRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(leftRegOp);
            }

            if (right instanceof ConstOp) { // 常值与RegOp计算，返回RegOp
                Integer right_c;
                if (right.needNegative) { // 处理负号
                    right_c = ((ConstOp) right).value;
                } else {
                    right_c = ((ConstOp) right).value;
                }

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, true, right_c, op);
                return new RegOp(retReg, leftRegOp.type, leftRegOp.isArray, leftRegOp.needNegative, leftRegOp.needNot);
            } else if (right instanceof RegOp) { // RegOp与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.needNegative) { // 处理负号
                    rightRegOp.regNo = CodeGenerater.CreatNegativeCode(rightRegOp.regNo);
                    rightRegOp.needNegative = false;
                }

                if (rightRegOp.type == 8) { // 类型转换i32
                    rightRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(rightRegOp);
                }

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, false, rightRegOp.regNo, op);
                return new RegOp(retReg, 32, false, false, false);
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
        Boolean posisReg = false;
        int pos;
        if (varOp.pos instanceof ConstOp) {
            pos = ((ConstOp) varOp.pos).value;
        } else {
            pos = ((RegOp) varOp.pos).regNo;
        }
        Register reg = IterateTK.cur_symTab.regMap.get(varOp.varSymbol);
        if (reg == null) {
            throw new RuntimeException("未插入寄存器表");
        }

        Pair ret = reg.getValueReg(posisReg, pos);
        if ((Boolean) ret.a.equals(true)) { // 拿到的是一个常值
            return new ConstOp((Integer) ret.b, varOp.needNegative, varOp.needNot);
        } else { // 拿到了值寄存器
            return new RegOp((Integer) ret.b, varOp.type, varOp.isArray, varOp.needNegative, varOp.needNot);
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
            return new RegOp(retReg, 32, false, funcOp.needNegative, funcOp.needNot);
        } else if (funcOp.funcSymbol.returnType.equals(FuncTypes.CharFunc)) {
            return new RegOp(retReg, 8, false, funcOp.needNegative, funcOp.needNot);
        } else { // void
            return null;
        }
    }

    /**
     * 获取size或pos的Exp
     * 
     * @param begin
     * @return a:]的下一位
     */
    public static Pair GetExpofSizeorPos(int begin) {
        int i = begin;
        int level = 1;
        while (level != 0) {
            if (IterateTK.token.get(i).tk.equals("["))
                level++;
            else if (IterateTK.token.get(i).tk.equals("]")) {
                level--;
            }
            i++;
        }
        return new Pair(i, GetExpfromIndex(begin, i - 2)); // i: ]的下一位
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

    public static Operands JudgeOperandsType(Operands operands, int type) {
        if (operands instanceof ConstOp) {
            return operands;
        } else {// RegOp
            RegOp regOp = (RegOp) operands;
            if (regOp.type != type) {
                operands = CodeGenerater.CreatTransTypeCode(operands);
            }
            return operands;
        }
    }

    /* ————————————————————————————————————————————————————————————————————— */
    /** 记录寄存器No */
    public static Integer regNum = 0;
    /** 记录在进入函数体时的全局寄存器No */
    public static Integer regNum_Record;

    public static void enterFuncBody() {
        regNum_Record = regNum;
        regNum = 0;
    }

    public static void quitFuncBody() {
        regNum = regNum_Record;
    }

    public static void setNeedTabTrue() {
        CodeGenerater.needTab = true;
    }

    public static void setNeedTabFalse() {
        CodeGenerater.needTab = false;
    }

    /**
     * 获取寄存器编号,并将寄存器编号加1
     * 
     * @return 返回寄存器编号
     */
    public static Integer getRegNum() {
        return regNum++;
    }

    /**
     * 向当前符号表中加入对应符号寄存器
     * 
     * @param varSymbol
     * @return
     */
    public static Register addSymboltoRegMap(VarSymbol varSymbol) {
        Boolean isArray = false;
        if (varSymbol.type.equals(VarTypes.IntArray) || varSymbol.type.equals(VarTypes.CharArray)
                || varSymbol.type.equals(VarTypes.ConstIntArray) || varSymbol.type.equals(VarTypes.ConstCharArray)) {
            isArray = true;
        }

        Integer type = 32;
        if (varSymbol.type.equals(VarTypes.Char) || varSymbol.type.equals(VarTypes.ConstChar)
                || varSymbol.type.equals(VarTypes.CharArray)
                || varSymbol.type.equals(VarTypes.ConstCharArray)) {
            type = 8;
        }

        Register reg = new Register(varSymbol, isArray, type);

        IterateTK.cur_symTab.regMap.put(varSymbol, reg);
        return reg;
    }

    /**
     * 初始化所有符号的所有valueReg和ConstValue寄存器
     */
    public static void initAllRegister() {
        for (Map.Entry<VarSymbol, Register> entry : IterateTK.cur_symTab.regMap.entrySet()) {
            entry.getValue().initAllConstandValueReg();
        }
    }

    /* ————————————————————————————————————————————————————————————————————— */

    public static ArrayList<ArrayList<Token>> Expsplits(ArrayList<Token> exps, String op) {
        ArrayList<ArrayList<Token>> ret = new ArrayList<>();
        int begin;

        for (int i = 0; i < exps.size(); i++) {
            begin = i;
            if (exps.get(i).str.equals(op)) {
                ret.add(GetSubExpfromIndex(begin, i - 1, exps));
            }

            if (i == exps.size() - 1) {
                ret.add(GetSubExpfromIndex(begin, i, exps));
            }
        }
        return ret;
    }

    /**
     * 判断是否有else块
     * 
     * @param pos 传入pos为if的Cond表达式的 )
     * @return
     */
    public static boolean JudgeElseBlock(int pos) {
        if (!IterateTK.getPosToken(pos + 1).str.equals("{")) { // if无{，仅有单行
            pos = findEndofScope(pos);
            if (!IterateTK.getPosToken(pos).tk.equals("ELSETK")) {
                return true;
            }
            return false;
        } else {// 有if块
            pos += 2; // { 的下一位
            int level = 1;
            while (pos < IterateTK.token.size()) {
                if (IterateTK.token.get(pos).tk.equals("{")) {
                    level++;
                } else if (IterateTK.token.get(pos).tk.equals("}")) {
                    level--;
                    if (level == 0) {
                        if (IterateTK.getPosToken(pos + 1).tk.equals("ELSETK")) {
                            return true;
                        }
                        return false;
                    }
                }
                pos++;
            }
            throw new RuntimeException("if找else时未找到匹配的}");
        }
    }

    public static int findEndofScope(int pos) {
        while (!IterateTK.getPosToken(pos).str.equals(";")) {
            pos++;
        }
        return pos;
    }

    public static void calOrExp(ArrayList<Token> orExps, Boolean haveElse, Boolean isIf) {
        ArrayList<ArrayList<Token>> andExps = Expsplits(orExps, "||");
        Pair p = null;

        for (int i = 0; i < andExps.size(); i++) {
            p = calAndExp(andExps.get(i), isIf);
            if (i != andExps.size() - 1) {
                CodeGenerater.CreatShortJumpCode_Or((String) p.b);
            } else { // 最后一个，确定全false跳转位置，并再次生成跳转指令
                if (isIf) {
                    if (haveElse) {
                        CodeGenerater.CreatShortJumpCode_Or(CodeGenerater.elseLabels.peek());
                    } else {
                        CodeGenerater.CreatShortJumpCode_Or(CodeGenerater.ifEndLabels.peek());
                    }
                } else {
                    CodeGenerater.CreatShortJumpCode_Or(CodeGenerater.forEndLabels.peek());
                }
            }
        }
    }

    public static Pair calAndExp(ArrayList<Token> andExp, Boolean isIf) {
        ArrayList<ArrayList<Token>> eqExps = Expsplits(andExp, "&&");
        String falseDest = null;
        Pair p = null;

        for (int i = 0; i < eqExps.size(); i++) {
            Operands temp = calEqExp(eqExps.get(i));
            int i1Reg = CodeGenerater.CreatTransi32toi1Code(temp);

            if (i != eqExps.size() - 1) {
                p = CodeGenerater.CreatShortJumpCode_And(i1Reg, null, falseDest);
            } else { // 最后一个，此时or的第一个可以确定是true，故确定跳转位置
                if (isIf)
                    p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.ifThenLabels.peek(), falseDest);
                else {
                    p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.forThenLabels.peek(), falseDest);
                }
            }

            falseDest = (String) p.b;
        }

        return p;
    }

    public static Operands callMidCodeGen(Operands left, Operands right, String relOp) {
        int regNo;
        boolean leftIsReg = false;
        boolean rightIsReg = false;
        int leftRegNo;
        int rightRegNo;

        if (left instanceof RegOp) {
            leftIsReg = true;
            leftRegNo = ((RegOp) left).regNo;
        } else {
            leftRegNo = ((ConstOp) left).value;
        }
        if (right instanceof RegOp) {
            rightIsReg = true;
            rightRegNo = ((RegOp) right).regNo;
        } else {
            rightRegNo = ((ConstOp) right).value;
        }

        if (left.type == 8) {
            left = CodeGenerater.CreatTransTypeCode(left);
        }
        if (right.type == 8) {
            right = CodeGenerater.CreatTransTypeCode(right);
        }

        regNo = CodeGenerater.CreatcalCondExp(leftIsReg, leftRegNo, rightIsReg, rightRegNo, relOp);
        left = new RegOp(regNo, 32, false, false, false);
        return left;
    }

    public static Operands calEqExp(ArrayList<Token> eqExp) {
        ArrayList<ArrayList<Token>> relExps = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        Deque<Operands> operands = new ArrayDeque<>();
        int begin;

        for (int i = 0; i < eqExp.size(); i++) {
            begin = i;
            if (eqExp.get(i).str.equals("==") || eqExp.get(i).str.equals("!=")) {
                ops.addLast(eqExp.get(i).str);
                relExps.add(GetSubExpfromIndex(begin, i - 1, eqExp));
            }

            if (i == eqExp.size() - 1) {
                relExps.add(GetSubExpfromIndex(begin, i, eqExp));
            }
        }

        for (ArrayList<Token> relExp : relExps) {
            operands.addLast(calRelExp(relExp));
        }

        Operands left = operands.getFirst();
        while (!ops.isEmpty()) {
            Operands right = operands.getFirst();
            String op = ops.getFirst();

            left = callMidCodeGen(left, right, op);
        }

        return left;
    }

    public static Operands calRelExp(ArrayList<Token> relExp) {
        ArrayList<ArrayList<Token>> commonExps = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        Deque<Operands> operands = new ArrayDeque<>();
        int begin;

        for (int i = 0; i < relExp.size(); i++) {
            begin = i;
            if (relExp.get(i).str.equals("<") || relExp.get(i).str.equals(">")
                    || relExp.get(i).str.equals("<=") || relExp.get(i).str.equals(">=")) {
                ops.addLast(relExp.get(i).str);
                commonExps.add(GetSubExpfromIndex(begin, i - 1, relExp));
            }

            if (i == relExp.size() - 1) {
                commonExps.add(GetSubExpfromIndex(begin, i, relExp));
            }
        }

        for (ArrayList<Token> commonExp : commonExps) {
            operands.addLast(calExp(commonExp, false));
        }

        Operands left = operands.getFirst();
        while (!ops.isEmpty()) {
            Operands right = operands.getFirst();
            String op = ops.getFirst();

            left = callMidCodeGen(left, right, op);
        }

        return left;
    }
}
