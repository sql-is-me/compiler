package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class utils {
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
     * 以传入符号表为父表造一个子表
     * 
     * @param parentId // 父符号表
     * 
     * @return 新生成的子表
     */
    public static void createSymTab(SymTab curSymTab) {
        SymTab child = new SymTab(++level, curSymTab);
        utils.curSymTab.childSymTabs.add(child);
        utils.curSymTab = child;
    }

    /**
     * 跳出当前子表
     */
    public static void jumpOutofBlock() {
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
            int paramNumber) {
        Symbol symbol = new FuncSymbol(curSymTab.id, name, returnType, paramTypes, paramNumber);
        curSymTab.curSymTab.put(name, symbol);
    }

    /**
     * 向当前符号表添加一变量符号
     * 
     * @param name  // 符号名
     * @param type  //符号类型
     * @param size  //数组长度
     * @param value //值
     */
    public static void addVarSymbol(String name, VarTypes type, int size, ArrayList<Integer> value) {
        Symbol symbol = new VarSymbol(curSymTab.id, name, type, size, value);
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
     * 从最近的上级符号表获取该symbol
     * 使用此函数前需先判断是否在当前符号表中，然后判断是否在前符号表中
     * 
     * @param name
     * @return Symbol
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

    // public static TokenType JudgeExpType(ArrayList<Token> exp) {
    // Token token;
    // TokenType type = null;
    // int expsize = exp.size();
    // for (int i = 0; i < expsize; i++) {
    // token = exp.get(i);

    // if (token.tk.equals("INTCON")) {
    // type = TokenType.Int;
    // } else if (token.tk.equals("CHARCON")) {
    // if (type.equals(null)) {
    // type = TokenType.Char;
    // } else {
    // // error
    // }
    // } else {
    // if (token.tk.equals("IDENFR")) {
    // TokenType ttype = ReturnIdentType(token.str);
    // if (ttype.equals(TokenType.IntArray) || ttype.equals(TokenType.CharArray)) {

    // } else if (ttype.equals(TokenType.Int) || ttype.equals(TokenType.Char)
    // || ttype.equals(TokenType.ConstInt)) {

    // }
    // }
    // }
    // }
    // return type;
    // }

    /**
     * 查找当前name的类型
     *
     * @param name 名字
     * 
     * @return String类型的type 若表中无该名字符号，则返回null
     */
    public static String ReturnType(String name) {
        Symbol symbol;
        String type;

        if (JudgeIdenfrExistNow(name)) {
            symbol = curSymTab.curSymTab.get(name);
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
     * 查找当前name的值
     *
     * @param name 名字
     * 
     * @return ArrayList<Integer>类型的value
     *         若非数组，则取第一个值即可
     *         若表中无该名字符号，则返回null
     */
    public static ArrayList<Integer> ReturnValue(String name) {
        Symbol symbol;
        ArrayList<Integer> values = null;

        if (JudgeIdenfrExistNow(name)) {
            symbol = curSymTab.curSymTab.get(name);
            if (symbol instanceof VarSymbol) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                values = varSymbol.value;
            }
        } else {
            if ((symbol = GetIdenfrfromBefore(name)) != null) {
                if (symbol instanceof VarSymbol) {
                    VarSymbol varSymbol = (VarSymbol) symbol;
                    values = varSymbol.value;
                }
            } else {
                values = null;
            }
        }

        return values;
    }
}
