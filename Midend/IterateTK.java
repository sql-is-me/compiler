package Midend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import Frontend.Lexer.Lexer.Token;
import Midend.Operands.ConstOp;
import Midend.Operands.Operands;
import Midend.Operands.RegOp;
import SymbolTable.FuncSymbol;
import SymbolTable.Register;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.SymTab;
import SymbolTable.Symbol;
import SymbolTable.VarSymbol;
import SymbolTable.VarSymbol.VarTypes;

public class IterateTK {
    /** 全局符号表 */
    public static SymTab global_symTab = SymbolTable.utils.globalSymTab;

    /** 当前符号表 */
    public static SymTab cur_symTab = global_symTab;

    /** 子符号表NO栈 */
    public static Stack<Integer> childSymTabNOs = new Stack<>();

    /** 作为token集合方便遍历 */
    public static ArrayList<Token> token = (ArrayList<Token>) Frontend.Lexer.Lexer.tokens;

    /** 位置 */
    public static Integer pos = 0;

    public static Token getNowToken() {
        return token.get(pos);
    }

    public static Token getPosToken(int position) {
        return token.get(position);
    }

    public static void initChildSymTabNOs() {
        childSymTabNOs.push(0);
    }

    public static void stepIntoChildSymTab() {
        Integer childSymTabNo = childSymTabNOs.pop();
        cur_symTab = cur_symTab.childSymTabs.get(childSymTabNo);
        childSymTabNOs.push(childSymTabNo + 1);
        childSymTabNOs.push(0);
        cur_symTab.regMap = new HashMap<>(cur_symTab.lastSymTab.regMap);
        utils.initAllRegister();
    }

    public static void stepOutfromChildSymTab() {
        cur_symTab = cur_symTab.lastSymTab;
        childSymTabNOs.pop();
    }

    /**
     * 开始生成中间代码
     */
    public static void StartGenerateMidCode() {
        // inits
        addLibFunc();
        initChildSymTabNOs();

        // 遍历
        GlobalSymbols();

        // 输出
        CodeGenerater.printfAllMidCodes();
    }

    /**
     * 向符号表中追加库函数，方便后续函数调用
     * 
     */
    public static void addLibFunc() {
        Symbol main = new FuncSymbol(global_symTab.id, "main", FuncTypes.IntFunc, new ArrayList<VarTypes>(), 0,
                -1, -1);
        global_symTab.curSymTab.put("main", main);

        Symbol getint = new FuncSymbol(global_symTab.id, "getint", FuncTypes.IntFunc, new ArrayList<VarTypes>(),
                0, -1, -1);
        global_symTab.curSymTab.put("getint", getint);

        Symbol getchar = new FuncSymbol(global_symTab.id, "getchar", FuncTypes.IntFunc, new ArrayList<VarTypes>(), 0,
                -1, -1);
        global_symTab.curSymTab.put("getchar", getchar);

        Symbol putint = new FuncSymbol(global_symTab.id, "putint", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.Int)), 1, -1, -1);
        global_symTab.curSymTab.put("putint", putint);

        Symbol putch = new FuncSymbol(global_symTab.id, "putch", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.Int)), 1, -1, -1);
        global_symTab.curSymTab.put("putch", putch);

        Symbol putstr = new FuncSymbol(global_symTab.id, "putstr", FuncTypes.VoidFunc,
                new ArrayList<VarTypes>(Arrays.asList(VarSymbol.VarTypes.CharArray)), 1, -1, -1);
        global_symTab.curSymTab.put("putstr", putstr);

        CodeGenerater.addLibFuncs();
    }

    /**
     * 遍历全局符号
     */
    public static void GlobalSymbols() {
        Symbol symbol;

        for (Map.Entry<String, Symbol> entry : cur_symTab.curSymTab.entrySet()) {
            symbol = entry.getValue();

            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                GloVarandArr(varSymbol);
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                GlobalFunc(funcSymbol);
                if (funcSymbol.name.equals("main")) {
                    break;
                }
            }
        }
    }

    public static void GloVarandArr(VarSymbol varSymbol) {
        utils.addSymboltoRegMap(varSymbol);

        pos = varSymbol.offset;

        if (varSymbol.size == 0) { // 非数组
            int value;
            pos++;

            if (getNowToken().str.equals("=")) {
                pos++;
                ArrayList<Token> initExp = getVarInitExp();
                ConstOp constOp = (ConstOp) utils.calExp(initExp, true);
                value = constOp.value;
            } else { // ;
                value = 0; // 初始化
            }

            int type;
            if (varSymbol.type.equals(VarSymbol.VarTypes.Int)
                    || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                type = 32;
            } else {
                type = 8;
            }
            CodeGenerater.declareGloVar(varSymbol.name, type, value);

        } else { // 数组
            ArrayList<Integer> values = new ArrayList<>(Collections.nCopies(varSymbol.size, 0));
            boolean needInitializer = false;
            pos++;

            while (!getNowToken().str.equals("]")) { // 跳到数组大小定义结束
                pos++;
            }
            pos++; // ]

            if (getNowToken().str.equals("=")) {
                pos++;

                if (getNowToken().str.equals("{")) { // 数组初始化
                    pos++;
                    ArrayList<ArrayList<Token>> initExps = getArrInitExp();
                    int i = 0;
                    for (ArrayList<Token> initExp : initExps) {
                        ConstOp constOp = (ConstOp) utils.calExp(initExp, true);
                        values.set(i, constOp.value);
                        i++;
                    }
                } else if (getNowToken().tk.equals("STRCON")) { // 字符串常量
                    String strConst = getNowToken().str;
                    for (int i = 0; i < strConst.length(); i++) {
                        values.set(i, (int) strConst.charAt(i));
                    }
                }
            } else {
                needInitializer = true;
            }

            if (varSymbol.type.equals(VarSymbol.VarTypes.IntArray)
                    || varSymbol.type.equals(VarSymbol.VarTypes.ConstIntArray)) {
                CodeGenerater.declareGloArr(varSymbol.name, 32, varSymbol.size, values, needInitializer);
            } else {
                CodeGenerater.declareGloArr(varSymbol.name, 8, varSymbol.size, values, needInitializer);
            }
        }
    }

    public static ArrayList<Token> getVarInitExp() { // FIXME 多参数的情况下需要确定 ，
        ArrayList<Token> initExp = new ArrayList<>();
        int level = 0;
        Token t = getNowToken();

        while (!t.str.equals(";") && !(t.str.equals(",") && level == 0)) {
            if (t.str.equals("(")) {
                level++;
            } else if (t.str.equals(")")) {
                level--;
            }

            initExp.add(getPosToken(pos));

            pos++;
            t = getNowToken();
        }

        return initExp;
    }

    public static ArrayList<ArrayList<Token>> getArrInitExp() {
        ArrayList<ArrayList<Token>> initExps = new ArrayList<>();
        boolean isInfunc = false;
        int level = 0;

        while (!getNowToken().str.equals("}")) {
            ArrayList<Token> initExp = new ArrayList<>();

            while (!getNowToken().str.equals(",") && !isInfunc) {
                if (getNowToken().tk.equals("IDENFR") && !isInfunc) {
                    Symbol s = utils.findSymbol(getNowToken().str);
                    if (s instanceof FuncSymbol) {
                        isInfunc = true;
                        level = 0;
                    }
                }

                if (isInfunc) {
                    if (getNowToken().str.equals("(") && isInfunc) {
                        level++;
                    } else if (getNowToken().str.equals(")") && isInfunc) {
                        level--;
                        if (level == 0) {
                            isInfunc = false;
                        }
                    }
                }

                if (!isInfunc && (getNowToken().str.equals(",") || getNowToken().str.equals("}"))) {
                    break;
                }
                initExp.add(getNowToken());
                pos++;
            }
            if (getNowToken().str.equals(",")) {
                pos++;
            }
            initExps.add(initExp);
        }
        pos++; // }
        return initExps;
    }

    public static void GlobalFunc(FuncSymbol funcSymbol) {
        stepIntoChildSymTab();
        CodeGenerater.CreatFuncHeadCode(funcSymbol);

        utils.getRegNum(); // 跳一个寄存器
        initFParams(funcSymbol.paramNumber); // 初始化分配
        if (funcSymbol.name.equals("main")) {
            while (!getNowToken().tk.equals("MAINTK")) {
                pos++;
            }
            pos += 4; // 跳到{ 的下一个Token;
        } else {
            pos = funcSymbol.offset + 1; // 跳到{ 的下一个Token
        }

        int retType;
        if (funcSymbol.returnType == FuncTypes.CharFunc) {
            retType = 8;
        } else if (funcSymbol.returnType == FuncTypes.IntFunc) {
            retType = 32;
        } else {
            retType = 0;
        }

        FuncBody(retType);

        CodeGenerater.CreatFuncEndCode();
    }

    public static void initFParams(int funcParamsCount) {
        Integer i = 0;
        if (funcParamsCount == 0) { // 无参直接返回
            return;
        }

        for (Map.Entry<String, Symbol> entry : cur_symTab.curSymTab.entrySet()) {
            Symbol symbol = entry.getValue();
            if (symbol instanceof VarSymbol) {
                Register reg = utils.addSymboltoRegMap((VarSymbol) symbol);
                if (reg.isArray) {
                    reg.initStoreArrReg(i.toString());
                } else {
                    reg.storeReg(true, 0, new RegOp(i, reg.type, false, false, false));
                }
            }

            i++;
            if (i == funcParamsCount) {
                break;
            }
        }
    }

    public static void FuncBody(int retType) {
        for (int level = 1;; pos++) {
            if (token.get(pos).str.equals("{")) {
                level++;
                stepIntoChildSymTab();
            } else if (token.get(pos).str.equals("}")) {
                stepOutfromChildSymTab();
                level--;
                if (level == 0) {
                    pos++;// }
                    break;
                }
            } else {
                Token t = getNowToken();
                if (t.str.equals(";")) {// 遇见分号跳过
                    continue;
                } else if (t.tk.equals("INTTK") || t.tk.equals("CHARTK")) { // 变量声明
                    pos++; // identifier
                    t = getNowToken();
                    VarSymbol varSymbol = (VarSymbol) utils.findSymbol(t.str);
                    declareLocalVarandArr(varSymbol);
                    while (getNowToken().str.equals(",")) {
                        pos++;
                        varSymbol = (VarSymbol) utils.findSymbol(getNowToken().str);
                        declareLocalVarandArr(varSymbol);
                    }
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("CONSTTK")) { // 常量声明
                    pos += 2; // identifier
                    t = getNowToken();
                    VarSymbol varSymbol = (VarSymbol) utils.findSymbol(t.str);
                    declareLocalVarandArr(varSymbol);
                    while (getNowToken().str.equals(",")) {
                        pos++;
                        varSymbol = (VarSymbol) utils.findSymbol(getNowToken().str);
                        declareLocalVarandArr(varSymbol);
                    }
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("PRINTFTK")) {
                    pos += 2; // ( strConst
                    processPrintf();
                } else if (t.tk.equals("IFTK")) {
                    pos += 2; // if (
                    ProcessIf(retType);
                } else if (t.tk.equals("FORTK")) {
                    pos += 2; // for (
                    ProcessFor(retType);
                } else if (t.tk.equals("RETURNTK")) {
                    if (retType == 0) {
                        CodeGenerater.CreatReturnCode(retType, false, 0);// ret void
                    } else {
                        int begin = pos + 1; // retExp
                        findEndofScope();
                        ArrayList<Token> retExp = utils.GetExpfromIndex(begin, pos - 1);
                        Operands operands = utils.calExp(retExp, false);

                        operands = utils.JudgeOperandsType(operands, retType);

                        if (operands instanceof ConstOp) {
                            CodeGenerater.CreatReturnCode(retType, true, ((ConstOp) operands).value);
                        } else {
                            CodeGenerater.CreatReturnCode(retType, false, ((RegOp) operands).regNo);
                        }
                    }
                } else { // LVal '=' Exp ';' && [Exp] ';'
                    int begin = pos;
                    int assignPos = 0;
                    ArrayList<Token> exp = new ArrayList<>();
                    while (true) {
                        t = getNowToken();

                        if (t.str.equals(";")) {
                            break;
                        }

                        if (t.str.equals("=")) {
                            assignPos = pos - begin;
                        }
                        exp.add(t);
                        pos++;
                    }

                    if (assignPos == 0) { // 单Exp
                        utils.calExp(exp, false); // 计算即可，不需要做额外处理
                    } else {
                        ArrayList<Token> LVal = utils.GetSubExpfromIndex(0, assignPos - 1, exp);
                        ArrayList<Token> Exp = utils.GetSubExpfromIndex(assignPos + 1, exp.size() - 1, exp);
                        Operands operands = utils.calExp(Exp, false);

                        VarSymbol varSymbol = (VarSymbol) utils.findSymbol(LVal.get(0).str);
                        Register reg = cur_symTab.regMap.get(varSymbol);

                        if (LVal.size() != 1) { // 对数组的某一个地方进行赋值
                            ArrayList<Token> posExp = utils.GetSubExpfromIndex(2, LVal.size(), LVal);
                            Operands posOp = utils.calExp(posExp, false);

                            int posV;
                            boolean posisConst = false;
                            if (posOp instanceof ConstOp) {
                                posisConst = true;
                                posV = ((ConstOp) posOp).value;
                            } else {
                                posV = ((RegOp) posOp).regNo;
                            }

                            reg.storeReg(posisConst, posV, operands);
                        } else { // 变量赋值
                            reg.storeReg(true, 0, operands);
                        }
                    }
                }
            }
        }
    }

    public static void declareLocalVarandArr(VarSymbol varSymbol) {
        Register reg = utils.addSymboltoRegMap(varSymbol);

        pos = varSymbol.offset;
        if (!getPosToken(pos + 1).str.equals("[")) { // 非数组
            Operands operands;
            pos++;

            if (getNowToken().str.equals("=")) {// 有初始赋值
                pos++;
                ArrayList<Token> initExp = getVarInitExp();
                operands = utils.calExp(initExp, false);
            } else { // ; 无初始赋值 or,需要再次进行
                operands = null;
            }

            if (operands != null) { // 有初始赋值
                reg.storeReg(true, 0, operands);
            }

        } else if (getPosToken(pos + 1).str.equals("[")) { // 数组
            pos += 2;
            pos = (Integer) utils.GetExpofSizeorPos(pos).a;

            if (getNowToken().str.equals("=")) { // 有初始化
                pos++;

                if (getNowToken().str.equals("{")) { // 数组初始化
                    pos++;
                    ArrayList<ArrayList<Token>> initExps = getArrInitExp();
                    int i = 0;
                    for (ArrayList<Token> initExp : initExps) {
                        Operands operands = utils.calExp(initExp, false);

                        reg.storeReg(true, i, operands);

                        i++;
                    }
                } else if (getNowToken().tk.equals("STRCON")) { // 字符串常量
                    String strConst = getNowToken().str;
                    for (int i = 0; i < strConst.length(); i++) {
                        reg.storeReg(true, i, new ConstOp((int) strConst.charAt(i), false, false));
                    }
                }
            } else {
                // 无初始化,不管
            }
        }
    }

    public static void processPrintf() {
        Token t = getNowToken();
        String str = t.str;

        int begin = pos + 2; // , Exp
        findEndofScope();

        ArrayList<Token> printfExp = utils.GetExpfromIndex(begin, pos - 2);
        ArrayList<Operands> paramsOperands = calPrintfExp(printfExp);

        StringBuilder sb = new StringBuilder();
        boolean escaping = false;
        int count = 0;
        int type;

        for (int i = 1; i < str.length() - 1; i++) {
            char currentChar = str.charAt(i);

            if (escaping) { // 转义中
                if (currentChar == 'n') {
                    sb.append("\n");
                } else {
                    sb.append(currentChar);
                }

                escaping = false;
            } else if (currentChar == '\\') { // 转义字符
                escaping = true;
            } else if (currentChar == '%' && i < str.length() - 1) {
                char nextChar = str.charAt(i + 1);
                if (nextChar == 'd' || nextChar == 'c') {
                    if (nextChar == 'c') {
                        type = 8;
                    } else {
                        type = 32;
                    }
                    i++;

                    if (sb.length() != 0) {
                        CodeGenerater.CreatPrintfStringCode(sb.toString());
                        sb = new StringBuilder();
                    }

                    Operands tempOp = utils.JudgeOperandsType(paramsOperands.get(count), 32); // 类型转换
                    CodeGenerater.CreatPrintfOperandsCode(type, tempOp);
                    count++;
                } else {
                    sb.append(currentChar);
                }
            } else {
                sb.append(currentChar);
            }
        }
        if (sb.length() != 0) {
            CodeGenerater.CreatPrintfStringCode(sb.toString());
        }
    }

    public static ArrayList<Operands> calPrintfExp(ArrayList<Token> exp) {
        ArrayList<Operands> printfOps = new ArrayList<>();
        ArrayList<ArrayList<Token>> Exps = new ArrayList<>();
        int level = 0;

        for (int i = 0; i < exp.size(); i++) {
            int begin = i;

            while (true) {
                if (exp.get(i).str.equals("(")) {
                    level++;
                } else if (exp.get(i).str.equals(")")) {
                    level--;
                }

                if (exp.get(i).str.equals(",") && level == 0) {
                    break;
                } else {
                    i++;
                }

                if (i == exp.size()) {
                    break;
                }
            }
            Exps.add(utils.GetSubExpfromIndex(begin, i - 1, exp));
        }

        for (ArrayList<Token> Exp : Exps) {
            Operands operands = utils.calExp(Exp, false);
            printfOps.add(operands);
        }

        return printfOps;
    }

    public static void findEndofScope() {
        while (!getNowToken().str.equals(";")) {
            pos++;
        }
    }

    public static void ProcessIf(int retType) {
        ArrayList<Token> condExp = new ArrayList<>();

        int level = 1;
        int begin = pos;
        while (pos < token.size()) {
            Token t = getNowToken();
            if (t.str.equals("(")) {
                level++;
            } else if (t.str.equals(")")) {
                level--;
                if (level == 0) {
                    condExp = utils.GetExpfromIndex(begin, pos - 1);
                    break;
                }
            }
            pos++;
        }
        boolean haveElse = utils.JudgeElseBlock(pos);

        utils.calOrExp(condExp, haveElse, true);
        CodeGenerater.CreatLabelTagCode(CodeGenerater.ifThenLabels.pop());

        stepIntoChildSymTab();
        pos++;
        boolean singleLine = false;
        if (!IterateTK.getPosToken(pos).str.equals("{")) { // if无{，仅有单行
            singleLine = true;
        } else {
            pos++;
        }

        StmtinForandIf(retType, singleLine);
        stepOutfromChildSymTab();

        if (haveElse) {
            CodeGenerater.CreatLabelTagCode(CodeGenerater.elseLabels.pop());
            pos += 2;// } else {
            stepIntoChildSymTab();
            StmtinForandIf(retType, singleLine);
            stepOutfromChildSymTab();
        }

        CodeGenerater.CreatLabelTagCode(CodeGenerater.ifEndLabels.pop());
    }

    public static void ProcessFor(int retType) {
        if (!getNowToken().str.equals(";")) {
            int begin = pos;
            while (!getNowToken().str.equals("=")) {
                pos++;
            }
            ArrayList<Token> LVal = utils.GetExpfromIndex(begin, pos - 1);
            begin = ++pos;
            while (!getNowToken().str.equals(";")) {
                pos++;
            }
            ArrayList<Token> Exp = utils.GetExpfromIndex(begin, pos - 1);
            Operands operands = utils.calExp(Exp, false);

            VarSymbol varSymbol = (VarSymbol) utils.findSymbol(LVal.get(0).str);
            Register reg = cur_symTab.regMap.get(varSymbol);

            if (LVal.size() != 1) { // 对数组的某一个地方进行赋值
                ArrayList<Token> posExp = utils.GetSubExpfromIndex(2, LVal.size(), LVal);
                Operands posOp = utils.calExp(posExp, false);

                int posV;
                boolean posisConst = false;
                if (posOp instanceof ConstOp) {
                    posisConst = true;
                    posV = ((ConstOp) posOp).value;
                } else {
                    posV = ((RegOp) posOp).regNo;
                }

                reg.storeReg(posisConst, posV, operands);
            } else { // 变量赋值
                reg.storeReg(true, 0, operands);
            }
        }
        pos++;

        stepIntoChildSymTab();

        Boolean haveCond = false;
        Boolean haveChange = false;
        ArrayList<Token> condExp = new ArrayList<>();
        if (!getNowToken().str.equals(";")) { // 第二个参数
            haveCond = true;
            int begin = pos;
            while (!getNowToken().str.equals(";")) {
                pos++;
            }

            condExp = utils.GetExpfromIndex(begin, pos - 1);
        }
        pos++;

        ArrayList<Token> changeExp = new ArrayList<>();
        if (!getNowToken().str.equals(")")) { // 第3个参数
            int begin = pos;
            int level = 1;

            while (true) {
                Token t = getNowToken();
                if (t.str.equals("(")) {
                    level++;
                } else if (t.str.equals(")")) {
                    level--;
                    if (level == 0) {
                        changeExp = utils.GetExpfromIndex(begin, pos - 1);
                        break;
                    }
                }
            }
        }
        pos++;

        CodeGenerater.CreatForFirstLabelCode(haveCond, haveChange);
        if (haveCond) {
            utils.calOrExp(condExp, false, false);
        }

        String forThenLabel = CodeGenerater.forThenLabels.pop();
        CodeGenerater.CreatLabelTagCode(forThenLabel);

        boolean singleLine = false;
        if (!getNowToken().str.equals("{")) {
            singleLine = true;
        } else {
            pos++;
        }

        StmtinForandIf(retType, singleLine);

        if (haveChange) { // 有第三个参数
            CodeGenerater.CreatbrCode(CodeGenerater.forChangeLabels.peek());
            CodeGenerater.CreatLabelTagCode(CodeGenerater.forChangeLabels.pop());

            int p = 0;
            for (int i = 0; i < changeExp.size(); i++) {
                if (!changeExp.get(i).str.equals("=")) {
                    i++;
                } else {
                    p = i;
                }
            }

            ArrayList<Token> LVal = utils.GetSubExpfromIndex(0, p - 1, changeExp);
            p++;

            ArrayList<Token> Exp = utils.GetSubExpfromIndex(p, changeExp.size() - 1, changeExp);
            Operands operands = utils.calExp(Exp, false);

            VarSymbol varSymbol = (VarSymbol) utils.findSymbol(LVal.get(0).str);
            Register reg = cur_symTab.regMap.get(varSymbol);

            if (LVal.size() != 1) { // 对数组的某一个地方进行赋值
                ArrayList<Token> posExp = utils.GetSubExpfromIndex(2, LVal.size(), LVal);
                Operands posOp = utils.calExp(posExp, false);

                int posV;
                boolean posisConst = false;
                if (posOp instanceof ConstOp) {
                    posisConst = true;
                    posV = ((ConstOp) posOp).value;
                } else {
                    posV = ((RegOp) posOp).regNo;
                }

                reg.storeReg(posisConst, posV, operands);
            } else { // 变量赋值
                reg.storeReg(true, 0, operands);
            }
        }

        if (haveCond) { // 跳转
            CodeGenerater.CreatbrCode(CodeGenerater.forCondLabels.pop());
        } else {
            CodeGenerater.CreatbrCode(forThenLabel);
        }

        CodeGenerater.CreatLabelTagCode(CodeGenerater.forEndLabels.pop());
    }

    public static void StmtinForandIf(int retType, boolean singleLine) {
        for (int level = 1;; pos++) {
            if (token.get(pos).str.equals("{")) {
                level++;
                stepIntoChildSymTab();
            } else if (token.get(pos).str.equals("}")) {
                stepOutfromChildSymTab();
                level--;
                if (level == 0) {
                    pos++;// }
                    break;
                }
            } else {
                Token t = getNowToken();
                if (t.str.equals(";")) {// 遇见分号跳过
                    continue;
                } else if (t.tk.equals("INTTK") || t.tk.equals("CHARTK")) { // 变量声明
                    pos++; // identifier
                    t = getNowToken();
                    VarSymbol varSymbol = (VarSymbol) utils.findSymbol(t.str);
                    declareLocalVarandArr(varSymbol);
                    while (getNowToken().str.equals(",")) {
                        pos++;
                        varSymbol = (VarSymbol) utils.findSymbol(getNowToken().str);
                        declareLocalVarandArr(varSymbol);
                    }
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("CONSTTK")) { // 常量声明
                    pos += 2; // identifier
                    t = getNowToken();
                    VarSymbol varSymbol = (VarSymbol) utils.findSymbol(t.str);
                    declareLocalVarandArr(varSymbol);
                    while (getNowToken().str.equals(",")) {
                        pos++;
                        varSymbol = (VarSymbol) utils.findSymbol(getNowToken().str);
                        declareLocalVarandArr(varSymbol);
                    }
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("PRINTFTK")) {
                    pos += 2; // ( strConst
                    processPrintf();
                } else if (t.tk.equals("IFTK")) {
                    pos += 2; // if (
                    ProcessIf(retType);
                } else if (t.tk.equals("FORTK")) {
                    pos += 2; // for (
                    ProcessFor(retType);
                } else if (t.tk.equals("BREAKTK")) {
                    pos++;// ;
                    CodeGenerater.CreatbrCode(CodeGenerater.forEndLabels.peek());
                } else if (t.tk.equals("CONTINUETK")) {
                    pos++;// ;
                    CodeGenerater.CreatbrCode(CodeGenerater.forChangeLabels.peek());
                } else if (t.tk.equals("RETURNTK")) {
                    if (retType == 0) {
                        CodeGenerater.CreatReturnCode(retType, false, 0);// ret void
                    } else {
                        int begin = pos + 1; // retExp
                        findEndofScope();
                        ArrayList<Token> retExp = utils.GetExpfromIndex(begin, pos - 1);
                        Operands operands = utils.calExp(retExp, false);

                        operands = utils.JudgeOperandsType(operands, retType);

                        if (operands instanceof ConstOp) {
                            CodeGenerater.CreatReturnCode(retType, true, ((ConstOp) operands).value);
                        } else {
                            CodeGenerater.CreatReturnCode(retType, false, ((RegOp) operands).regNo);
                        }
                    }
                } else if (t.tk.equals("RETURNTK")) {
                    if (retType == 0) {
                        CodeGenerater.CreatReturnCode(retType, false, 0);// ret void
                    } else {
                        int begin = pos + 1; // retExp
                        findEndofScope();
                        ArrayList<Token> retExp = utils.GetExpfromIndex(begin, pos - 1);
                        Operands operands = utils.calExp(retExp, false);

                        operands = utils.JudgeOperandsType(operands, retType);

                        if (operands instanceof ConstOp) {
                            CodeGenerater.CreatReturnCode(retType, true, ((ConstOp) operands).value);
                        } else {
                            CodeGenerater.CreatReturnCode(retType, false, ((RegOp) operands).regNo);
                        }
                    }
                } else { // LVal '=' Exp ';' && [Exp] ';'
                    int begin = pos;
                    int assignPos = 0;
                    ArrayList<Token> exp = new ArrayList<>();
                    while (true) {
                        t = getNowToken();

                        if (t.str.equals(";")) {
                            break;
                        }

                        if (t.str.equals("=")) {
                            assignPos = pos - begin;
                        }
                        exp.add(t);
                        pos++;
                    }

                    if (assignPos == 0) { // 单Exp
                        utils.calExp(exp, false); // 计算即可，不需要做额外处理
                    } else {
                        ArrayList<Token> LVal = utils.GetSubExpfromIndex(0, assignPos - 1, exp);
                        ArrayList<Token> Exp = utils.GetSubExpfromIndex(assignPos + 1, exp.size() - 1, exp);
                        Operands operands = utils.calExp(Exp, false);

                        VarSymbol varSymbol = (VarSymbol) utils.findSymbol(LVal.get(0).str);
                        Register reg = cur_symTab.regMap.get(varSymbol);

                        if (LVal.size() != 1) { // 对数组的某一个地方进行赋值
                            ArrayList<Token> posExp = utils.GetSubExpfromIndex(2, LVal.size(), LVal);
                            Operands posOp = utils.calExp(posExp, false);

                            int posV;
                            boolean posisConst = false;
                            if (posOp instanceof ConstOp) {
                                posisConst = true;
                                posV = ((ConstOp) posOp).value;
                            } else {
                                posV = ((RegOp) posOp).regNo;
                            }

                            reg.storeReg(posisConst, posV, operands);
                        } else { // 变量赋值
                            reg.storeReg(true, 0, operands);
                        }
                    }
                }

                if (singleLine) {
                    findEndofScope();
                    pos++; // ;
                    return;
                }
            }
        }
    }
}
