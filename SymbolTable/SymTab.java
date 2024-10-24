package SymbolTable;

import java.util.*;

public class SymTab {
    int count = 0;

    public int id;
    public int parentId;
    public int level; // 层次

    // key: name value: Symbol
    public SymTab lastSymTab;
    public LinkedHashMap<String, Symbol> curSymTab;
    public ArrayList<SymTab> childSymTabs;

    public SymTab(int parentId, int level, SymTab lastSymTab) {
        this.id = count++;
        this.parentId = parentId;
        this.level = level;
        this.lastSymTab = lastSymTab;
        this.curSymTab = new LinkedHashMap<String, Symbol>();
        this.childSymTabs = new ArrayList<SymTab>();
    }
}
