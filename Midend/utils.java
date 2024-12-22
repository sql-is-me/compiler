package Midend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.Stack;

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
        Stack<Character> opStack = new Stack<>();
        Character op = '+';

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);

            if (t.tk.equals("INTCON") || t.tk.equals("CHRCON")) { // 常量处理
                Operands temp;
                if (t.tk.equals("INTCON"))
                    temp = new ConstOp(Integer.parseInt(t.str), opStack);
                else {
                    temp = new ConstOp((int) t.str.charAt(1), opStack);
                }

                operands.addLast(temp);

                op = ' ';
                opStack = new Stack<>();
            } else if (t.str.equals("+") || t.str.equals("-") || t.str.equals("*") || t.str.equals("/")
                    || t.str.equals("%") || t.str.equals("!")) { // 表达式符号处理

                if (op != ' ' && t.str.equals("-")) { // 处理UnaryOP -
                    opStack.push('-');
                } else if (op != ' ' && t.str.equals("+")) { // 处理UnaryOP +
                    continue;
                } else if (op != ' ' && t.str.equals("!")) { // 处理UnaryOP +
                    opStack.push('!');
                } else {
                    op = t.str.charAt(0);
                    ops.addLast(op);
                }
            } else if (t.tk.equals("IDENFR") || t.tk.equals("GETINTTK") || t.tk.equals("GETCHARTK")) { // 变量处理
                Symbol symbol = findSymbol(t.str);

                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    Operands temp;
                    if (i < exp.size() - 1 && exp.get(i + 1).str.equals("[")) { // 数组位置处理
                        i += 2;// [

                        int level = 1;
                        int begin = i;
                        while (level != 0) {
                            if (exp.get(i).str.equals("[")) {
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

                        boolean canGetConstValue = false; // 如果他pos是一个常数，并且对应位置有值的话，直接返回
                        if (tempOp instanceof ConstOp && IterateTK.cur_symTab.regMap.containsKey(varSymbol)) {
                            int constPos = ((ConstOp) tempOp).value;
                            Register reg = IterateTK.cur_symTab.regMap.get(varSymbol);
                            if (reg.size != -1 && reg.constValue.get(constPos) != Integer.MIN_VALUE) {
                                temp = new ConstOp(reg.constValue.get(constPos), opStack);
                                canGetConstValue = true;
                            } else {
                                temp = createNewVarOp(varSymbol, tempOp, opStack);// 原部分
                            }
                        } else {
                            temp = createNewVarOp(varSymbol, tempOp, opStack);
                        }

                        // if (!canGetConstValue) {
                        // temp = createNewVarOp(varSymbol, tempOp, opStack);
                        // }
                    } else if (varSymbol.type.equals(VarTypes.IntArray) || varSymbol.type.equals(VarTypes.CharArray)
                            || varSymbol.type.equals(VarTypes.ConstIntArray)
                            || varSymbol.type.equals(VarTypes.ConstCharArray)) { // 传递指针，至pos为-1
                        temp = createNewVarOp(varSymbol, new ConstOp(-1, new Stack<>()), opStack);
                    } else { // 变量处理，pos返回 0 ConstOp即可
                        if (IterateTK.cur_symTab.regMap.get(varSymbol).size != -1
                                && IterateTK.cur_symTab.regMap.get(varSymbol).constValue.get(0) != Integer.MIN_VALUE) {
                            temp = new ConstOp(IterateTK.cur_symTab.regMap.get(varSymbol).constValue.get(0),
                                    new Stack<>());
                        } else {
                            temp = createNewVarOp(varSymbol, new ConstOp(0, new Stack<>()), opStack);
                        }
                    }

                    operands.addLast(temp);
                } else {
                    FuncSymbol funcSymbol = (FuncSymbol) symbol;

                    i += 2; // indentifier (
                    int begin = i;
                    for (int level = 1; i < exp.size(); i++) {
                        if (exp.get(i).str.equals("(")) {
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

                    Integer type = 0;
                    if (funcSymbol.returnType.equals(FuncTypes.IntFunc)) {
                        type = 32;
                    } else {
                        type = 8;
                    }
                    Operands temp = new FuncOp(funcSymbol, type, paramsOps, opStack);
                    operands.addLast(temp);
                }

                op = ' ';
                opStack = new Stack<>();
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
                opStack = new Stack<>();
            }
        }

        return

        processCal(operands, ops, isGlobalInit);
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

        if (left == null) { // 调用一返回值未void的函数
            return null;
        }

        if (left.opStack.size() != 0) { // 处理前缀符号
            left = handleOpStack(left);
        }

        return left;
    }

    public static Operands handleOpStack(Operands operands) {
        if (operands instanceof ConstOp) {
            while (operands.opStack.size() != 0) {
                Character op = operands.opStack.pop();
                if (op == '!') {
                    if (((ConstOp) operands).value != 0) {
                        ((ConstOp) operands).value = 0;
                    } else {
                        ((ConstOp) operands).value = 1;
                    }
                } else {
                    ((ConstOp) operands).value = -((ConstOp) operands).value;
                }
            }
            return operands;
        } else {
            Integer tRegNo = ((RegOp) operands).regNo;
            while (operands.opStack.size() != 0) {
                Character op = operands.opStack.pop();
                if (op == '!') {
                    tRegNo = CodeGenerater.CreatNotCode(tRegNo);
                } else {
                    tRegNo = CodeGenerater.CreatNegativeCode(tRegNo);
                }
            }
            return new RegOp(tRegNo, 32, false, new Stack<>());
        }
    }

    public static Operands readyforTransExpCode(Operands left, Operands right, Character op) {
        if (left.opStack.size() != 0) { // 处理前缀符号
            left = handleOpStack(left);
        }
        if (right.opStack.size() != 0) { // 处理前缀符号
            right = handleOpStack(right);
        }

        if (left instanceof ConstOp) {
            Integer left_c = ((ConstOp) left).value;

            if (right instanceof ConstOp) { // 双常值直接计算返回即可
                Integer right_c = ((ConstOp) right).value;

                return new ConstOp(ConstCal(left_c, right_c, op), new Stack<>());
            } else if (right instanceof RegOp) { // 常值与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.type == 8) { // 类型转换i32
                    rightRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(rightRegOp);
                }

                Integer retReg = CodeGenerater.CreatCalExp(true, left_c, false, rightRegOp.regNo, op);
                return new RegOp(retReg, rightRegOp.type, rightRegOp.isArray, new Stack<>());
            }
        } else if (left instanceof RegOp) {
            RegOp leftRegOp = (RegOp) left;

            if (leftRegOp.type == 8) { // 类型转换i32
                leftRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(leftRegOp);
            }

            if (right instanceof ConstOp) { // 常值与RegOp计算，返回RegOp
                Integer right_c = ((ConstOp) right).value;

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, true, right_c, op);
                return new RegOp(retReg, leftRegOp.type, leftRegOp.isArray, new Stack<>());
            } else if (right instanceof RegOp) { // RegOp与RegOp计算，返回RegOp
                RegOp rightRegOp = (RegOp) right;

                if (rightRegOp.type == 8) { // 类型转换i32
                    rightRegOp = (RegOp) CodeGenerater.CreatTransTypeCode(rightRegOp);
                }

                Integer retReg = CodeGenerater.CreatCalExp(false, leftRegOp.regNo, false, rightRegOp.regNo, op);
                return new RegOp(retReg, 32, false, new Stack<>());
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
            posisReg = true;
        }
        Register reg = IterateTK.cur_symTab.regMap.get(varOp.varSymbol);
        if (reg == null) {
            throw new RuntimeException("未插入寄存器表");
        }

        if (pos == -1) { // 传递数组指针作为参数
            String sReg = reg.getArrPointer();
            return new RegOp(Integer.valueOf(sReg), varOp.type, varOp.isArray, varOp.opStack);
        } else {
            Pair ret = reg.getValueReg(posisReg, pos);
            if ((Boolean) ret.a.equals(true)) { // 拿到的是一个常值
                return new ConstOp((Integer) ret.b, new Stack<>());
            } else { // 拿到了值寄存器
                return new RegOp((Integer) ret.b, varOp.type, varOp.isArray, varOp.opStack);
            }
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

        cleanAllReg();
        if (funcOp.funcSymbol.returnType.equals(FuncTypes.IntFunc)) {
            return new RegOp(retReg, 32, false, funcOp.opStack);
        } else if (funcOp.funcSymbol.returnType.equals(FuncTypes.CharFunc)) {
            return new RegOp(retReg, 8, false, funcOp.opStack);
        } else { // void
            return null;
        }
    }

    public static void cleanAllReg() {
        for (Register reg : IterateTK.cur_symTab.regMap.values()) {
            reg.initAllConstandValueReg();
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
            if (IterateTK.token.get(i).str.equals("["))
                level++;
            else if (IterateTK.token.get(i).str.equals("]")) {
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

        for (int i = 0, level = 0; i < subexp.size() && count < paramNum - 1; i++) { // 前面paramNum-1个参数
            if (subexp.get(i).str.equals("(")) {
                level++;
            } else if (subexp.get(i).str.equals(")")) {
                level--;
            }

            if (subexp.get(i).str.equals(",") && level == 0) {
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

    public static void initAllRegister_Strong() {
        for (Map.Entry<VarSymbol, Register> entry : IterateTK.cur_symTab.regMap.entrySet()) {
            entry.getValue().initAllConstandValueReg();
            if (entry.getValue().isArray && entry.getValue().size != -1) {
                entry.getValue().stackReg = "-1";
            }
        }
    }

    /* ————————————————————————————————————————————————————————————————————— */

    public static ArrayList<ArrayList<Token>> Expsplits(ArrayList<Token> exps, String op) {
        ArrayList<ArrayList<Token>> ret = new ArrayList<>();
        int begin = 0;

        for (int i = 0; i < exps.size(); i++) {

            if (exps.get(i).str.equals(op)) {
                ret.add(GetSubExpfromIndex(begin, i - 1, exps));
                begin = i + 1;
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
            if (IterateTK.getPosToken(pos + 1).tk.equals("ELSETK")) {
                return true;
            }
            return false;
        } else {// 有if块
            pos += 2; // { 的下一位
            int level = 1;
            while (pos < IterateTK.token.size()) {
                if (IterateTK.token.get(pos).str.equals("{")) {
                    level++;
                } else if (IterateTK.token.get(pos).str.equals("}")) {
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
            if (i == andExps.size() - 1) {
                p = calAndExp(andExps.get(i), isIf, true, haveElse);
            } else {
                p = calAndExp(andExps.get(i), isIf, false, haveElse);
            }

            if (i != andExps.size() - 1) {
                CodeGenerater.CreatShortJumpCode_Or((String) p.b);
                utils.initAllRegister(); // 初始化所有寄存器，确保跳转的寄存器不会影响到其他部分
            }
        }
    }

    public static Pair calAndExp(ArrayList<Token> andExp, Boolean isIf, Boolean thelast, Boolean haveElse) {
        ArrayList<ArrayList<Token>> eqExps = Expsplits(andExp, "&&");
        String falseDest = null;
        Pair p = null;

        for (int i = 0; i < eqExps.size(); i++) {
            Operands temp = calEqExp(eqExps.get(i));
            int i1Reg = CodeGenerater.CreatTransi32toi1Code(temp);

            if (i != eqExps.size() - 1) {
                if (thelast) {
                    if (isIf) {
                        if (haveElse) {
                            p = CodeGenerater.CreatShortJumpCode_And(i1Reg, null, CodeGenerater.elseLabels.peek());
                        } else {
                            p = CodeGenerater.CreatShortJumpCode_And(i1Reg, null, CodeGenerater.ifEndLabels.peek());
                        }
                    } else {
                        p = CodeGenerater.CreatShortJumpCode_And(i1Reg, null, CodeGenerater.forEndLabels.peek());
                    }
                } else {
                    p = CodeGenerater.CreatShortJumpCode_And(i1Reg, null, falseDest);
                }
            } else { // 最后一个，此时or的第一个可以确定是true，故确定跳转位置
                if (thelast) {
                    if (isIf) {
                        if (haveElse) {
                            p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.ifThenLabels.peek(),
                                    CodeGenerater.elseLabels.peek());
                        } else {
                            p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.ifThenLabels.peek(),
                                    CodeGenerater.ifEndLabels.peek());
                        }
                    } else {
                        p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.forThenLabels.peek(),
                                CodeGenerater.forEndLabels.peek());
                    }
                } else {
                    if (isIf)
                        p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.ifThenLabels.peek(), falseDest);
                    else {
                        p = CodeGenerater.CreatShortJumpCode_And(i1Reg, CodeGenerater.forThenLabels.peek(), falseDest);
                    }
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
        left = new RegOp(regNo, 1, false, new Stack<>());
        return left;
    }

    public static Operands calEqExp(ArrayList<Token> eqExp) {
        ArrayList<ArrayList<Token>> relExps = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        Deque<Operands> operands = new ArrayDeque<>();
        int begin = 0;

        for (int i = 0; i < eqExp.size(); i++) {

            if (eqExp.get(i).str.equals("==") || eqExp.get(i).str.equals("!=")) {
                ops.addLast(eqExp.get(i).str);
                relExps.add(GetSubExpfromIndex(begin, i - 1, eqExp));
                begin = i + 1;
            }

            if (i == eqExp.size() - 1) {
                relExps.add(GetSubExpfromIndex(begin, i, eqExp));
            }
        }

        for (ArrayList<Token> relExp : relExps) {
            operands.addLast(calRelExp(relExp));
        }

        Operands left = operands.pollFirst();
        while (!ops.isEmpty()) {
            Operands right = operands.pollFirst();
            String op = ops.pollFirst();

            left = callMidCodeGen(left, right, op);
        }

        return left;
    }

    public static Operands calRelExp(ArrayList<Token> relExp) {
        ArrayList<ArrayList<Token>> commonExps = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();
        Deque<Operands> operands = new ArrayDeque<>();
        int begin = 0;

        for (int i = 0; i < relExp.size(); i++) {

            if (relExp.get(i).str.equals("<") || relExp.get(i).str.equals(">")
                    || relExp.get(i).str.equals("<=") || relExp.get(i).str.equals(">=")) {
                ops.addLast(relExp.get(i).str);
                commonExps.add(GetSubExpfromIndex(begin, i - 1, relExp));
                begin = i + 1;
            }

            if (i == relExp.size() - 1) {
                commonExps.add(GetSubExpfromIndex(begin, i, relExp));
            }
        }

        for (ArrayList<Token> commonExp : commonExps) {
            operands.addLast(calExp(commonExp, false));
        }

        Operands left = operands.pollFirst();
        while (!ops.isEmpty()) {
            Operands right = operands.pollFirst();
            String op = ops.pollFirst();

            left = callMidCodeGen(left, right, op);
        }

        return left;
    }

    public static Operands createNewVarOp(VarSymbol varSymbol, Operands pos, Stack<Character> opStack) {
        Integer type = 0;
        Boolean isArray = false;
        if (varSymbol.type.equals(VarTypes.Int) || varSymbol.type.equals(VarTypes.ConstInt)) {
            type = 32;
        } else if (varSymbol.type.equals(VarTypes.Char) || varSymbol.type.equals(VarTypes.ConstChar)) {
            type = 8;
        } else if (varSymbol.type.equals(VarTypes.IntArray) || varSymbol.type.equals(VarTypes.ConstIntArray)) {
            type = 32;
            isArray = true;
        } else if (varSymbol.type.equals(VarTypes.CharArray) || varSymbol.type.equals(VarTypes.ConstCharArray)) {
            type = 8;
            isArray = true;
        }

        return new VarOp(varSymbol, type, isArray, pos, opStack);
    }

    /**
     * 看栈头的condLabel是否是当前for循环的，是则返回true
     * 
     * @return
     */
    public static boolean JudgeForChangeExist() {
        if (CodeGenerater.forChangeLabels.size() != 0) {
            String forEndLabel = CodeGenerater.forEndLabels.peek();
            String tempArr[] = forEndLabel.split("\\.");
            String lastPart = tempArr[tempArr.length - 1];
            int forNo = Integer.parseInt(lastPart);

            String forChangeLabel = CodeGenerater.forChangeLabels.peek();
            String tempArr2[] = forChangeLabel.split("\\.");
            String lastPart2 = tempArr2[tempArr2.length - 1];
            int forNo2 = Integer.parseInt(lastPart2);

            if (forNo == forNo2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 看栈头的condLabel是否是当前for循环的，是则返回true
     *
     * @return
     */
    public static boolean JudgeForCondExist() {
        if (CodeGenerater.forCondLabels.size() != 0) {
            String forEndLabel = CodeGenerater.forEndLabels.peek();
            String tempArr[] = forEndLabel.split("\\.");
            String lastPart = tempArr[tempArr.length - 1];
            int forNo = Integer.parseInt(lastPart);

            String forCondLabel = CodeGenerater.forCondLabels.peek();
            String tempArr2[] = forCondLabel.split("\\.");
            String lastPart2 = tempArr2[tempArr2.length - 1];
            int forNo2 = Integer.parseInt(lastPart2);

            if (forNo == forNo2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }
}
