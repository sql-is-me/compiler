package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Children.Tools;

import java.util.Map;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class utils {
    public enum TokenTypes {
        IntFunc,
        CharFunc,
        VoidFunc,
        ConstInt,
        ConstChar,
        Int,
        Char,
        IntArray,
        CharArray,
        ConstIntArray,
        ConstCharArray,
        Undefined
    }

    private static int level = 0; // 层次

    /** SymTab的Id计数用 */
    public static int idCount_Symbol = 0;
    /** SymTab的Id计数用 */
    public static int idCount_SymTab = 0;

    /** 最外层符号表 */
    public static SymTab globalSymTab = new SymTab(++level, null);
    /** 当前符号表 */
    public static SymTab curSymTab = globalSymTab;

    /**
     * 以当前符号表为父表造一个子表
     * 
     * @param curSymTab // 父符号表
     * 
     * @return 新生成的子表
     */
    public static void createSymTab(SymTab curSymTab) {
        SymTab child = new SymTab(++level, curSymTab);
        utils.curSymTab.childSymTabs.add(child);
        utils.curSymTab = child;
        setInGlobal();
    }

    /**
     * 跳出当前子表
     */
    public static void jumpOutSymTab() {
        setInGlobal();
        curSymTab = curSymTab.lastSymTab;
        level--;
    }

    /**
     * 向当前符号表添加一个函数符号
     * 
     * @param name        // 符号名
     * @param returnType  //返回类型
     * @param paramTypes  //参数类型
     * @param paramNumber // 参数个数
     */
    public static void addFuncSymbol(String name, FuncTypes returnType, ArrayList<VarTypes> paramTypes,
            int paramNumber, int offset) {
        SymTab recordSymtab = curSymTab;// 先跳回上一级符号表，加入函数相关信息
        curSymTab = curSymTab.lastSymTab;

        Symbol symbol = new FuncSymbol(curSymTab.id, name, returnType, paramTypes, paramNumber, recordSymtab.id,
                offset);
        curSymTab.curSymTab.put(name, symbol);

        curSymTab = recordSymtab; // 再跳回去
    }

    /**
     * 向当前符号表添加一变量符号
     * 
     * @param name  // 符号名
     * @param type  //符号类型
     * @param size  //数组长度
     * @param value //值
     */
    public static void addVarSymbol(String name, VarTypes type, int size, Boolean isGlobal, Boolean isConst,
            int offset) {
        Symbol symbol = new VarSymbol(curSymTab.id, name, type, size, isGlobal, isConst, offset);
        curSymTab.curSymTab.put(name, symbol);
    }

    /**
     * 输出结果用map
     */
    private static TreeMap<Integer, ArrayList<String>> ansMap = new TreeMap<>();

    /**
     * 遍历所有符号表
     * 将遍历结果加入到ansMap中
     * 
     * @param globalSymTab
     */
    public static void VisitAllSymTabs(SymTab globalSymTab) {
        VisitSymTab(globalSymTab.id, globalSymTab.curSymTab); // get globleSymTab's symbol
        ToChildSymTab(globalSymTab); // 递归找到所有子符号表
    }

    /**
     * 递归遍历子符号表
     * 将子符号表中的symbol加入到ansMap中
     * 
     * @param curSymTab
     */
    private static void ToChildSymTab(SymTab curSymTab) { //
        ArrayList<SymTab> childSymTab = curSymTab.childSymTabs;

        if (childSymTab.size() != 0) {
            for (SymTab symTab : childSymTab) {
                VisitSymTab(symTab.id, symTab.curSymTab);
                ToChildSymTab(symTab);
            }
        }
    }

    /**
     * 获取cursymMap的symbol
     * 
     * @param tabId  表Id
     * @param symMap 待遍历的符号表的curSymMap
     */
    public static void VisitSymTab(Integer tabId, LinkedHashMap<String, Symbol> symMap) {
        for (Map.Entry<String, Symbol> entry : symMap.entrySet()) {
            Symbol symbol = entry.getValue();
            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                addValuetoTreeMap(tabId, varSymbol.toString());
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                addValuetoTreeMap(tabId, funcSymbol.toString());
            }
        }
    }

    /**
     * 向TreeMap加入值,
     * 若存在则map.get(key).add(value),
     * 若不存在则map.put(key, new ArrayList<>());
     * 
     * @param key
     * @param value
     */
    private static void addValuetoTreeMap(Integer key, String value) {
        ansMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * 输出symbol.txt函数
     * 利用LinkedHashMap完成sort后输出到对应ArrayList
     * 
     * @return ArrayList<String> ansArray
     */
    public static ArrayList<String> WriteAnstoArray() {
        ArrayList<String> ansArray = new ArrayList<>();

        for (Map.Entry<Integer, ArrayList<String>> entry : ansMap.entrySet()) {
            String key = entry.getKey().toString();
            for (String str : entry.getValue()) {
                ansArray.add(key + str + "\n");
            }
        }

        return ansArray;
    }

    /**
     * 判断该name是否已经在当前符号表中存在
     *
     * @param name 名字
     * 
     * @return boolean
     */
    public static boolean JudgeIdenfrExistNow(String name) {
        if (curSymTab.curSymTab.containsKey(name)) {
            return true;
        }
        return false;
    }

    /**
     * 判断该name是否已经在之前符号表中存在
     * 
     * @param name
     * @return boolean
     */
    public static boolean JudgeIdenfrExistBefore(String name) {
        SymTab findSymTab = curSymTab.lastSymTab;
        while (findSymTab != null) {
            if (findSymTab.curSymTab.containsKey(name)) {
                return true;
            } else {
                findSymTab = findSymTab.lastSymTab;
            }
        }
        return false;
    }

    /**
     * 判断该重定义的符号是否之前被定义为全局函数,并且该符号所处位置不是全局
     * 
     * @param name 重定义的符号名
     */
    public static void JudgeIdenfrisGlobalFunc(String name) {
        SymTab findSymTab = globalSymTab;

        if (!globalSymTab.equals(curSymTab) && findSymTab.curSymTab.containsKey(name)) {
            Symbol findSymbol = findSymTab.curSymTab.get(name);
            if (findSymbol instanceof FuncSymbol) {
                repeatSymbolisFunc = true;
            }
        }
        repeatSymbolisFunc = false;
    }

    /** 用以判断是否重定义 */
    private static boolean isRepeat = false;

    /** 用以判断符号是否之前被定义为全局函数 */
    private static boolean repeatSymbolisFunc = false;

    /**
     * 获取用定义判断条件
     * 
     * @return 如果该indent已经被定义返回true
     */
    public static boolean GetRepeat() {
        return isRepeat;
    }

    /**
     * 获取被重定义的符号之前是否为全局函数
     * 
     * @return 如果是返回true
     */
    public static boolean GetRepeatSymbolisFunc() {
        return repeatSymbolisFunc;
    }

    /**
     * 重复性判断，并作报错处理
     * 
     * @param token 符号token
     * @return 重复时返回true，不重复时返回false
     */
    public static boolean JudgeRepeat(Token token) {
        // JudgeIdenfrisGlobalFunc(token.str);

        if (JudgeIdenfrExistNow(token.str)) {
            isRepeat = true;
            ErrorLog.makelog_error(token.line, 'b');
            return true;
        }
        isRepeat = false;
        return false;
    }

    /** 判断该标识符是否被定义，以帮助本行的错误排查 */
    public static boolean isdefined = false;

    /**
     * 未定义判断，并作报错处理
     * 
     * @param token 符号token
     * @return 不存在时返回true，存在时返回false
     */
    public static boolean JudgeUndefined(Token token) {
        if (!JudgeIdenfrExistNow(token.str) && !JudgeIdenfrExistBefore(token.str)) {
            ErrorLog.makelog_error(token.line, 'c');
            isdefined = false;
            return true;
        }
        isdefined = true;
        return false;
    }

    /** 判断函数类型以及是否在函数体中 */
    private static FuncTypes funcType = null;
    /** 判断是否找到return */
    private static boolean findReturn = false;
    /** 用以存储函数体的SymTab，判断是否到达函数最外层 */
    private static SymTab funcSymTab = null;

    /** 进入函数触发，将funcType转变为当前函数类型,将funcSymTab转变为当前函数所在符号表 */
    public static void SetfuncType(FuncTypes funcType) {
        findReturn = false;
        utils.funcType = funcType;
        utils.funcSymTab = utils.curSymTab;
    }

    /** 退出函数触发，将funcType和funcSymTab转变为Null */
    public static void SetfuncTypeandSymTabtoNull() {
        utils.funcType = null;
        utils.funcSymTab = null;
    }

    /**
     * 经过return时触发，判断是否在函数体符号表中且下一个token是}
     * 若是将findReturn转为true
     */
    public static void findReturn(Token RBRACEToken) {
        if (RBRACEToken.tk.equals("RBRACE") && utils.curSymTab.equals(funcSymTab)) {
            findReturn = true;
        }
    }

    /**
     * 判断函数类型为非Void时有无返回语句,并在结束后将findReturn置为false，相关参数设置为null
     * 若在最后一句(即当前SymTab等于函数体的SymTab，并且下一个Token是'}')不存在返回语句，则写错误日志
     */
    public static void JudgeReturnExist(Token returnToken) {
        if (!funcType.equals(FuncTypes.VoidFunc)) {
            if (!findReturn) {
                ErrorLog.makelog_error(returnToken.line, 'g');
            }
        }
        SetfuncTypeandSymTabtoNull();
    }

    /**
     * 判断当前函数体是否是void类型 如果是判断是否有返回其他类型
     * 若是则写错误日志
     * 
     * @param returnToken
     * @param nextToken
     * @return
     */
    public static void JudgeReturnUnmatch(Token returnToken, Token nextToken) {
        if (funcType.equals(FuncTypes.VoidFunc)) { // void
            if (!nextToken.tk.equals("SEMICN")) {
                ErrorLog.makelog_error(returnToken.line, 'f');
            }
        }
    }

    /**
     * 从上级符号表获取该symbol
     * 
     * @param name
     * @return Symbol 若无返回null
     */
    public static Symbol GetIdenfrfromBefore(String name) {
        SymTab findSymTab = curSymTab.lastSymTab;
        while (findSymTab != null) {
            if (findSymTab.curSymTab.containsKey(name)) {
                return findSymTab.curSymTab.get(name);
            } else {
                findSymTab = findSymTab.lastSymTab;
            }
        }
        return null;
    }

    /**
     * 从当前符号表获取该symbol
     * 
     * @param name
     * @return Symbol 若无返回null
     */
    public static Symbol GetIdenfrfromNow(String name) {
        Symbol findSymbol = curSymTab.curSymTab.get(name);
        return findSymbol;
    }

    /**
     * 从全局符号表获取该symbol
     * 
     * @param name
     * @return Symbol 若无返回null
     */
    public static Symbol GetIdenfrfromGlobal(String name) {
        Symbol findSymbol = globalSymTab.curSymTab.get(name);
        return findSymbol;
    }

    /**
     * 直接从当前和上级符号表获取该符号，并返回Symbol
     * 
     * @param name 符号名
     * @return symbol 当未定义时返回null
     */
    public static Symbol GetIdenfr(String name) {
        Symbol symbol;
        if ((symbol = GetIdenfrfromNow(name)) == null) {
            symbol = GetIdenfrfromBefore(name);
        }
        return symbol;
    }

    /**
     * 判断LVal是否为常量 若是则写错误
     * 
     * @param varType LVal的类型
     * @param line    LVal所在行
     */
    public static void JudgeLValisConst(VarTypes varType, int line) {
        if (varType == null) // LVal未定义，已经报过错，故跳过
        {
            return;
        }

        if (varType.equals(VarTypes.ConstInt) || varType.equals(VarTypes.ConstIntArray)
                || varType.equals(VarTypes.ConstChar)
                || varType.equals(VarTypes.ConstCharArray)) {
            ErrorLog.makelog_error(line, 'h');
        }
    }

    /**
     * 查找当前name的类型
     *
     * @param name 名字
     * 
     * @return String类型的type
     */
    public static String ReturnType(String name) {
        Symbol symbol;
        String type;

        if ((symbol = GetIdenfrfromNow(name)) != null) {
            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                type = varSymbol.type.toString();
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                type = funcSymbol.returnType.toString();
            }
        } else {
            if ((symbol = GetIdenfrfromBefore(name)) != null) {
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    type = varSymbol.type.toString();
                } else {
                    FuncSymbol funcSymbol = (FuncSymbol) symbol;
                    type = funcSymbol.returnType.toString();
                }
            } else {
                type = null;
            }
        }

        return type;
    }

    /**
     * 判断调用该函数时的参数数量是否满足该函数的参数数量要求
     * 如果不满足则写错误日志
     * 
     * @param funcSymbol        函数符号
     * @param funcRParamsNumber 调用时传入的参数个数
     * @param token             函数名token
     */
    public static boolean CheckFuncRParamsNumber(FuncSymbol funcSymbol, int funcRParamsNumber, Token token) {
        if (funcRParamsNumber != funcSymbol.paramNumber) {
            ErrorLog.makelog_error(token.line, 'd');
            return false;
        }
        return true;
    }

    /** 循环嵌套层数 */
    private static int loopLevel = 0;

    /** 进入循环，层数自增 */
    public static void Inloop() {
        loopLevel++;
    }

    /** 退出循环 */
    public static void Outloop() {
        loopLevel--;
    }

    /**
     * 判断当前是否在循环中 若不在循环中，则写错误日志
     * 
     * @param token break或continue的token
     */
    public static void JudgeInLoop(Token token) {
        if (loopLevel == 0) {
            ErrorLog.makelog_error(token.line, 'm');
        }
    }

    /**
     * 用以完成获得Exp类型的函数，规则见CalculateType
     * 
     * @param exp ExpToken的List
     * @return TokenTypes的String
     */
    public static String JudgeExpType(ArrayList<Token> exp) {
        ArrayList<Token> childList = null;
        Token token;
        TokenTypes tempType = null;
        TokenTypes type = null;
        int expsize = exp.size();
        String expType = null;

        for (int i = 0; i < expsize; i += 2) {
            token = exp.get(i);

            if (token.tk.equals("INTCON")) {
                type = CalculateType(type, TokenTypes.Int);
            } else if (token.tk.equals("CHRCON")) {
                type = CalculateType(type, TokenTypes.Char);
            } else if (token.tk.equals("IDENFR")) {
                Symbol symbol = GetIdenfr(token.str);
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    tempType = TokenTypes.valueOf(varSymbol.type.toString());
                } else if (symbol instanceof FuncSymbol) {
                    FuncSymbol funcSymbol = (FuncSymbol) symbol;
                    tempType = TokenTypes.valueOf(funcSymbol.returnType.toString());
                } else {
                    tempType = TokenTypes.Undefined;
                }

                if (isArray(tempType) && i < exp.size() - 1 && exp.get(i + 1).tk.equals("LBRACK")) {
                    tempType = RemoveConstandFunc(tempType);

                    i += 2;// indent [
                    childList = new ArrayList<>();
                    int level = 1;
                    while (level != 0 && i < expsize) {
                        if (exp.get(i).str.equals("]")) {
                            level--;
                            if (level == 0) {
                                break;
                            }
                        } else if (exp.get(i).str.equals("[")) {
                            level++;
                        }
                        childList.add(exp.get(i));
                        i++;
                    }

                    expType = JudgeExpType(childList);
                    TokenTypes tempType2 = TokenTypes.valueOf(expType);

                    if (tempType2.equals(TokenTypes.Int)) {
                        if (tempType.equals(TokenTypes.IntArray)) {
                            tempType = TokenTypes.Int;
                        } else {
                            tempType = TokenTypes.Char;
                        }
                    } else {
                        System.err.println("Array index is not int");
                    }

                } else if (isFunc(tempType)) {
                    i += 2; // jump to (
                    int level = 1; // ()层数
                    while (level != 0 && i < expsize) {
                        if (exp.get(i).str.equals(")")) {
                            level--;
                        } else if (exp.get(i).str.equals("(")) {
                            level++;
                        }
                        i++;
                    }
                    i--; // 回退到)
                }

                type = CalculateType(type, tempType);

            } else if (token.str.equals("(")) {
                childList = new ArrayList<>();
                int level = 1; // ()层数
                i++;

                while (level != 0 && i < expsize) {
                    if (exp.get(i).str.equals(")")) {
                        level--;
                        if (level == 0) {
                            break;
                        }
                    } else if (exp.get(i).str.equals("(")) {
                        level++;
                    }
                    childList.add(exp.get(i));
                    i++;
                }

                expType = JudgeExpType(childList);
                tempType = TokenTypes.valueOf(expType);
                type = CalculateType(type, tempType);

            } else if (token.str.equals("+") || token.str.equals("-")) { // UnaryOp
                while ((exp.get(i).str.equals("+") || exp.get(i).str.equals("-")) && i < expsize) {
                    i++;
                }
                i -= 2;
                continue;
            } else {
                System.err.println("error when calType in Exp");
            }
        }
        return type.toString();
    }

    /**
     * 判断该type是什么类型
     * 
     * @param tokenType
     * @return 如果是Array返回true
     */
    private static boolean isArray(TokenTypes tokenType) {
        if (tokenType == null) {
            return false;
        }

        if (tokenType.equals(TokenTypes.IntArray) || tokenType.equals(TokenTypes.CharArray)
                || tokenType.equals(TokenTypes.ConstIntArray) || tokenType.equals(TokenTypes.ConstCharArray)) {
            return true;
        }
        return false;
    }

    /**
     * 判断该type是什么类型
     * 
     * @param tokenType
     * @return 如果是Var返回true
     */
    private static boolean isVar(TokenTypes tokenType) {
        if (tokenType == null) {
            return false;
        }

        if (tokenType.equals(TokenTypes.Int) || tokenType.equals(TokenTypes.Char)
                || tokenType.equals(TokenTypes.ConstInt) || tokenType.equals(TokenTypes.ConstChar)) {
            return true;
        }
        return false;
    }

    /**
     * 判断该type是什么类型
     * 
     * @param tokenType
     * @return 如果是func返回true
     */
    private static boolean isFunc(TokenTypes tokenType) {
        if (tokenType == null) {
            return false;
        }

        if (tokenType.equals(TokenTypes.IntFunc) || tokenType.equals(TokenTypes.CharFunc)
                || tokenType.equals(TokenTypes.VoidFunc)) {
            return true;
        }
        return false;
    }

    /**
     * 用以快速计算Exp类型
     * int int -> int |
     * int char -> int |
     * char char -> char |
     * char int - > int |
     * 
     * Var Array -> error |
     * Array Var -> error |
     * Array Array -> error |
     * Var Null -> error |
     * Array Null -> error |
     * Null else -> else |
     * Null !Null -> error |
     * 
     * @param before
     * @param temp
     * @return TokenTypes
     */
    private static TokenTypes CalculateType(TokenTypes before, TokenTypes temp) {
        if (temp.equals(TokenTypes.Undefined)) {
            return TokenTypes.Undefined;
        }

        before = RemoveConstandFunc(before);
        temp = RemoveConstandFunc(temp);

        if (isVar(before)) {
            if (isArray(temp)) { // 不能与数组做计算
                System.err.println("var Array");
                return null;
            } else if (temp == null) { // temp is null
                System.err.println("temp is null");
                return null;
            }

            if (before.equals(TokenTypes.Int)) {// Int Var -> Int
                return TokenTypes.Int;
            } else {
                if (temp.equals(TokenTypes.Int)) { // char int -> int
                    return TokenTypes.Int;
                } else { // char char -> char
                    return TokenTypes.Char;
                }
            }
        } else if (isArray(before)) {
            System.err.println("before is Array");
            return null;
        } else { // before is null
            return temp;
        }
    }

    /**
     * 去除掉Const和Func，将其转变为普通类型
     * 
     * @param now
     * @return TokenTypes int，char，intArray，charArray
     */
    private static TokenTypes RemoveConstandFunc(TokenTypes now) {
        if (now == null) {
            return null;
        }

        if (now.equals(TokenTypes.ConstInt)) {
            now = TokenTypes.Int;
        } else if (now.equals(TokenTypes.ConstChar)) {
            now = TokenTypes.Char;
        } else if (now.equals(TokenTypes.ConstIntArray)) {
            now = TokenTypes.IntArray;
        } else if (now.equals(TokenTypes.ConstCharArray)) {
            now = TokenTypes.CharArray;
        } else if (now.equals(TokenTypes.IntFunc)) {
            now = TokenTypes.Int;
        } else if (now.equals(TokenTypes.CharFunc)) {
            now = TokenTypes.Char;
        } else if (now.equals(TokenTypes.VoidFunc)) {
            now = null;
        }
        return now;
    }

    public static void JudgeFuncRParamsCorrect(Token funcIdent, int funcRParamsNumber,
            ArrayList<VarTypes> paramsTypes) {
        FuncSymbol funcSymbol = (FuncSymbol) GetIdenfrfromGlobal(funcIdent.str); // 从全局表开始找，局部变量可能会覆盖函数

        if (!isdefined) {
            return;
        }

        if (!CheckFuncRParamsNumber(funcSymbol, funcRParamsNumber, funcIdent)) {
            return;
        }

        for (int i = 0; i < funcSymbol.paramTypes.size(); i++) {
            VarTypes needType = funcSymbol.paramTypes.get(i);
            VarTypes loadType = paramsTypes.get(i);

            if (needType.equals(loadType)) {
                continue;
            } else if ((needType.equals(VarTypes.Int) && loadType.equals(VarTypes.Char))
                    || (loadType.equals(VarTypes.Int) && needType.equals(VarTypes.Char))) {
                continue;
            } else {
                ErrorLog.makelog_error(funcIdent.line, 'e');
                return;
            }
        }
    }

    /**
     * 分析并获得printf语句中所需要的参数类型
     * 
     * @param stringToken STRCON
     * @return ArrayList<VarTypes> 所需参数类型List
     */
    public static ArrayList<VarTypes> AnalysisPrintString(Token stringToken) {
        ArrayList<VarTypes> printfParamsTypes = new ArrayList<>();

        String str = stringToken.str;
        int size = str.length();
        Character cc;
        for (int i = 0; i < size; i++) {
            cc = str.charAt(i);
            if (cc.equals('%')) {
                if (i < size - 1 && (str.charAt(i + 1) == 'd' || str.charAt(i + 1) == 'c')) {
                    i++;
                    cc = str.charAt(i);
                    if (cc == 'd') {
                        printfParamsTypes.add(VarTypes.Int);
                    } else {
                        printfParamsTypes.add(VarTypes.Char);
                    }
                }
            }
        }

        return printfParamsTypes;
    }

    /**
     * 判断printf所需的参数是否与传入的参数数量是否相同
     * 若不吻合写错误日志
     * 
     * @param printfToken     printf的token
     * @param needParamsTypes 所需的参数类型List
     * @param paramsTypes     传入的参数类型List
     */
    public static void JudgePrintfParamsCorrect(Token printfToken, ArrayList<VarTypes> needParamsTypes,
            ArrayList<VarTypes> paramsTypes) {

        if (needParamsTypes.size() != paramsTypes.size()) {
            ErrorLog.makelog_error(printfToken.line, 'l');
            return;
        }
    }

    public static void setInGlobal() {
        if (!curSymTab.equals(globalSymTab)) {
            Tools.inGlobal = false;
        } else {
            Tools.inGlobal = true;
        }
    }
}
