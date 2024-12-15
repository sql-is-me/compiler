import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

import Frontend.Lexer.Lexer.Token;
import Operands.ConstOp;
import Operands.Operands;
import Operands.RegOp;
import SymbolTable.FuncSymbol;
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
        utils.addSymboltoRegMap(varSymbol, true);

        pos = varSymbol.offset;

        if (varSymbol.size == 0) { // 非数组
            int value;
            pos++;

            if (getNowToken().str.equals("=")) {
                pos++;
                ArrayList<Token> initExp = getVarInitExp(pos);
                ConstOp constOp = (ConstOp) utils.calExp(initExp, true);
                value = constOp.value;
            } else { // ;
                value = 0;
            }

            if (varSymbol.type.equals(VarSymbol.VarTypes.Int)
                    || varSymbol.type.equals(VarSymbol.VarTypes.ConstInt)) {
                CodeGenerater.declareGloVar(varSymbol.name, 32, value);
            } else {
                CodeGenerater.declareGloVar(varSymbol.name, 8, value);
            }
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
                    ArrayList<ArrayList<Token>> initExps = getArrInitExp(pos);
                    int i = 0;
                    for (ArrayList<Token> initExp : initExps) {
                        ConstOp constOp = (ConstOp) utils.calExp(initExp, true);
                        values.set(i, constOp.value);
                        i++;
                    }
                } else { // 字符串常量
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

    public static ArrayList<Token> getVarInitExp(Integer pos) {
        ArrayList<Token> initExp = new ArrayList<>();
        Token t = getPosToken(pos);

        while (!t.str.equals(";")) {
            initExp.add(getPosToken(pos));

            pos++;
            t = getPosToken(pos);
        }

        return initExp;
    }

    public static ArrayList<ArrayList<Token>> getArrInitExp(Integer pos) {
        ArrayList<ArrayList<Token>> initExps = new ArrayList<>();
        Token t = getPosToken(pos);
        boolean isInfunc = false;
        int level = 0;

        while (!t.str.equals("}")) {
            ArrayList<Token> initExp = new ArrayList<>();

            while (!t.str.equals(",") || isInfunc) {
                if (t.tk.equals("IDENFR") && !isInfunc) {
                    Symbol s = utils.findSymbol(t.str);
                    if (s instanceof FuncSymbol) {
                        isInfunc = true;
                        level = 0;
                    }
                }

                if (isInfunc) {
                    if (t.tk.equals("(") && isInfunc) {
                        level++;
                    } else if (t.tk.equals(")") && isInfunc) {
                        level--;
                        if (level == 0) {
                            isInfunc = false;
                        }
                    }
                }

                if (!isInfunc && (t.str.equals(",") || t.str.equals("}"))) {
                    break;
                }
                initExp.add(t);
                pos++;
                t = getPosToken(pos);
            }
            initExps.add(initExp);
        }
        return initExps;
    }

    public static void GlobalFunc(FuncSymbol funcSymbol) {
        stepIntoChildSymTab();
        CodeGenerater.CreatFuncHeadCode(funcSymbol);

        utils.getRegNum(); // 跳一个寄存器
        initFParams(funcSymbol.paramNumber); // 初始化分配
        pos = funcSymbol.offset + 1; // 跳到{ 的下一个Token

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
        stepOutfromChildSymTab();
    }

    public static void initFParams(int funcParamsCount) {
        Integer i = 0;
        if (funcParamsCount == 0) { // 无参直接返回
            return;
        }

        for (Map.Entry<String, Symbol> entry : cur_symTab.curSymTab.entrySet()) {
            Symbol symbol = entry.getValue();
            if (symbol instanceof VarSymbol) {
                Register reg = utils.addSymboltoRegMap((VarSymbol) symbol, false);
                if (reg.isArray) {
                    reg.storeReg_Arr(i.toString());
                } else {
                    reg.storeReg_simple(0, false, i);
                }
            }

            i++;
            if (i == funcParamsCount) {
                break;
            }
        }
    }

    public static void FuncBody(int retType) {
        int level = 1; // 层次
        for (;; pos++) {
            if (token.get(pos).str.equals("{")) {
                level++;
                stepIntoChildSymTab();
            } else if (token.get(pos).str.equals("}")) {
                stepOutfromChildSymTab();
                level--;
                if (level == 0) {
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
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("CONSTTK")) { // 常量声明
                    pos += 2; // identifier
                    t = getNowToken();
                    VarSymbol varSymbol = (VarSymbol) utils.findSymbol(t.str);
                    declareLocalVarandArr(varSymbol);
                    findEndofScope(); // 跳到句子尾部
                } else if (t.tk.equals("PRINTFTK")) {
                    pos += 2; // ( strConst
                    processPrintf();
                } else if (t.tk.equals("IFTK")) {

                } else if (t.tk.equals("ELSETK")) {

                } else if (t.tk.equals("FORTK")) {

                } else if (t.tk.equals("BREAKTK")) {

                } else if (t.tk.equals("CONTINUETK")) {

                } else if (t.tk.equals("RETURNTK")) {
                    if (retType == 0) {
                        CodeGenerater.CreatReturnCode(retType, false, 0);// ret void
                    } else {
                        int begin = pos + 1; // retExp
                        findEndofScope();
                        ArrayList<Token> retExp = utils.GetExpfromIndex(begin, pos);
                        Operands operands = utils.calExp(retExp, false);

                        operands = utils.JudgeOperandsType(operands, retType);

                        if (operands instanceof ConstOp) {
                            CodeGenerater.CreatReturnCode(retType, false, ((ConstOp) operands).value);
                        } else {
                            CodeGenerater.CreatReturnCode(retType, false, ((RegOp) operands).regNo);
                        }
                    }
                } else { // LVal '=' Exp ';' && [Exp] ';'

                }
            }
        }

    }

    public static void declareLocalVarandArr(VarSymbol varSymbol) {
        Register reg = utils.addSymboltoRegMap(varSymbol, false);

        pos = varSymbol.offset;
        if (!getNowToken().str.equals("[")) { // 非数组
            Operands operands;
            pos++;

            if (getNowToken().str.equals("=")) {// 有初始赋值
                pos++;
                ArrayList<Token> initExp = getVarInitExp(pos);
                operands = utils.calExp(initExp, false);
            } else { // ; 无初始赋值
                operands = null;
            }

            if (operands != null) { // 有初始赋值
                boolean isConst;
                int valueORvReg;

                if (operands instanceof ConstOp) {
                    valueORvReg = ((ConstOp) operands).value;
                    isConst = true;
                } else {
                    operands = utils.JudgeOperandsType(operands, reg.type);

                    valueORvReg = ((RegOp) operands).regNo;
                    isConst = false;
                }

                reg.storeReg_simple(0, isConst, valueORvReg);
            }

        } else { // 数组
            pos = (Integer) utils.GetExpofSizeorPos(pos).a;

            if (getNowToken().str.equals("=")) { // 有初始化
                pos++;

                if (getNowToken().str.equals("{")) { // 数组初始化
                    pos++;
                    ArrayList<ArrayList<Token>> initExps = getArrInitExp(pos);
                    int i = 0;
                    for (ArrayList<Token> initExp : initExps) {
                        Operands operands = utils.calExp(initExp, true);
                        if (operands instanceof RegOp) {
                            if (varSymbol.type.equals(VarTypes.IntArray)
                                    || varSymbol.type.equals(VarTypes.ConstIntArray)) { // 类型判断
                                operands = utils.JudgeOperandsType(operands, 32);
                            } else if (varSymbol.type.equals(VarTypes.CharArray)
                                    || varSymbol.type.equals(VarTypes.ConstCharArray)) {
                                operands = utils.JudgeOperandsType(operands, 8);
                            }

                            reg.storeReg_simple(i, false, ((RegOp) operands).regNo);
                        } else {
                            reg.storeReg_simple(i, true, ((ConstOp) operands).value);
                        }

                        i++;
                    }
                } else { // 字符串常量
                    String strConst = getNowToken().str;
                    for (int i = 0; i < strConst.length(); i++) {
                        reg.storeReg_simple(i, true, (int) strConst.charAt(i));
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

        ArrayList<Token> printfExp = utils.GetExpfromIndex(begin, pos - 1);
        ArrayList<Operands> paramsOperands = calPrintfExp(printfExp);

        StringBuilder sb = new StringBuilder();
        int count = 0;
        int type;

        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);

            if (currentChar == '%' && i < str.length() - 1) {
                char nextChar = str.charAt(i + 1);
                if (nextChar == 'd' || nextChar == 'c') {
                    if (nextChar == 'c') {
                        type = 8;
                    } else {
                        type = 32;
                    }
                    i++;

                    CodeGenerater.CreatPrintfStringCode(sb.toString());

                    Operands tempOp = utils.JudgeOperandsType(paramsOperands.get(count), type); // 类型转换
                    CodeGenerater.CreatPrintfOperandsCode(type, tempOp);
                    count++;
                } else {
                    sb.append(currentChar);
                }
            } else {
                sb.append(currentChar);
            }
        }

        CodeGenerater.CreatPrintfStringCode(sb.toString());
    }

    public static ArrayList<Operands> calPrintfExp(ArrayList<Token> exp) {
        ArrayList<Operands> printfOps = new ArrayList<>();
        ArrayList<ArrayList<Token>> Exps = new ArrayList<>();
        int level = 0;

        for (int i = 0; i < exp.size(); i++) {
            Token t = exp.get(i);
            int begin = i;

            while ((!t.str.equals(",") || level > 0)) {
                if (t.tk.equals("(")) {
                    level++;
                } else if (t.tk.equals(")")) {
                    level--;
                }
                i++;

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

}
