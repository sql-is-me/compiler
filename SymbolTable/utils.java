package SymbolTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class utils {
    public static int level = 0;

    public SymTab createSymTab(int parentId, SymTab curSymTab) {
        return new SymTab(parentId, ++level, curSymTab);
    }

    public void jumpOutofBlock() {
        level--;
    }

    LinkedHashMap<Integer, String> ansMap = new LinkedHashMap<>(); // ans

    public void VisitAllSymTabs(SymTab globalSymTab) {
        VisitAllSymTabs(globalSymTab); // get globleSymTab's symbol
        ToChildSymTab(globalSymTab); // 递归找到所有子符号表
    }

    private void ToChildSymTab(SymTab curSymTab) { // 递归便利子符号表
        ArrayList<SymTab> childSymTab = curSymTab.childSymTabs;

        while (childSymTab.size() != 0) {
            for (SymTab symTab : childSymTab) {
                VisitSymTab(symTab.level, symTab.curSymTab);
                ToChildSymTab(symTab);
            }
        }
    }

    public void VisitSymTab(Integer level, LinkedHashMap<String, Symbol> symMap) { // 获取cursymMap的symbol
        for (Map.Entry<String, Symbol> entry : symMap.entrySet()) {
            Symbol symbol = entry.getValue();
            ansMap.put(level, symbol.toString());
        }
    }

    public void WriteAns() { // output func
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
}
