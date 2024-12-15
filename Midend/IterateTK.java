import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

import Frontend.Lexer.Lexer.Token;
import Operands.ConstOp;
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

        FuncBody();

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

    public static void FuncBody() {

    }

}
