package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import frontend.Lexer.Lexer.Token;
import SymbolTable.Symbol.TokenType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class utils {
    private static int level = 0;
    public static final SymTab globalSymTab = createSymTab(0, null); // 最外层符号表
    public static SymTab curSymTab = globalSymTab;

    public static SymTab createSymTab(int parentId) {
        return new SymTab(parentId, ++level, curSymTab);
    }

    public static SymTab createSymTab(int parentId, SymTab curSymTab) {
        return new SymTab(parentId, ++level, curSymTab);
    }

    public static void jumpOutofBlock() {
        curSymTab = curSymTab.lastSymTab;
        level--;
    }

    public static void addSymbol(String name, TokenType type, ArrayList<Integer> value, int size,
            ArrayList<TokenType> funcParaTypes) {
        Symbol symbol;

        symbol = new Symbol(curSymTab.id, name, type, value, size, funcParaTypes);
        curSymTab.curSymTab.put(name, symbol);
    }

    private static LinkedHashMap<Integer, String> ansMap = new LinkedHashMap<>(); // ans

    public static void VisitAllSymTabs(SymTab globalSymTab) {
        VisitAllSymTabs(globalSymTab); // get globleSymTab's symbol
        ToChildSymTab(globalSymTab); // 递归找到所有子符号表
    }

    private static void ToChildSymTab(SymTab curSymTab) { // 递归遍历子符号表
        ArrayList<SymTab> childSymTab = curSymTab.childSymTabs;

        while (childSymTab.size() != 0) {
            for (SymTab symTab : childSymTab) {
                VisitSymTab(symTab.level, symTab.curSymTab);
                ToChildSymTab(symTab);
            }
        }
    }

    public static void VisitSymTab(Integer level, LinkedHashMap<String, Symbol> symMap) { // 获取cursymMap的symbol
        for (Map.Entry<String, Symbol> entry : symMap.entrySet()) {
            Symbol symbol = entry.getValue();
            ansMap.put(level, symbol.toString());
        }
    }

    public static void WriteAns() { // output func
        Map<Integer, String> sortedMap = ansMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // 仍然使用 LinkedHashMap 保持顺序
                ));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("symbol.txt"))) {
            for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {
                bw.write(entry.getKey().toString() + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("error when running parser");
            System.err.println("could not open + symbol.txt");
        }
    }

    public static boolean JudgeIdenfrExistNow(String name) { // 判断该name是否已经在当前符号表中存在
        if (curSymTab.curSymTab.containsKey(name)) {
            return true;
        }
        return false;
    }

    public static boolean JudgeIdenfrExistBefore(String name) { // 判断该name是否已经在之前符号表中存在
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

    public static Symbol GetIdenfrfromBefore(String name) { // 判断该name是否已经在之前符号表中存在
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

    public static TokenType JudgeExpType(ArrayList<Token> exp) {
        Token token;
        TokenType type = null;
        int expsize = exp.size();
        for (int i = 0; i < expsize; i++) {
            token = exp.get(i);

            if (token.tk.equals("INTCON")) {
                type = TokenType.Int;
            } else if (token.tk.equals("CHARCON")) {
                if (type.equals(null)) {
                    type = TokenType.Char;
                } else {
                    // error
                }
            } else {
                if (token.tk.equals("IDENFR")) {
                    TokenType ttype = ReturnIdentType(token.str);
                    if (ttype.equals(TokenType.IntArray) || ttype.equals(TokenType.CharArray)) {

                    } else if (ttype.equals(TokenType.Int) || ttype.equals(TokenType.Char)
                            || ttype.equals(TokenType.ConstInt)) {

                    }
                }
            }
        }
        return type;
    }

    public static TokenType ReturnIdentType(String name) {
        Symbol symbol;
        TokenType type;
        if (JudgeIdenfrExistNow(name)) {
            symbol = curSymTab.curSymTab.get(name);
            type = symbol.type;
        } else {
            if ((symbol = GetIdenfrfromBefore(name)) != null) {
                type = symbol.type;
            } else {
                type = null;
            }
        }
        return type;
    }
}
