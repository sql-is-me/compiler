package SymbolTable;

import java.util.*;

public class SymTab {
    public int id;
    public int parentId;
    public int level; // 层次

    // key: name value: Symbol
    public SymTab lastSymTab;
    public LinkedHashMap<String, Symbol> curSymTab;
    public ArrayList<SymTab> childSymTabs;

    public HashMap<VarSymbol, Register> regMap;

    public SymTab(int level, SymTab lastSymTab) {
        this.id = ++utils.idCount_SymTab;
        this.level = level;
        this.lastSymTab = lastSymTab;
        this.curSymTab = new LinkedHashMap<String, Symbol>();
        this.childSymTabs = new ArrayList<SymTab>();
        this.regMap = new HashMap<VarSymbol, Register>();
    }
}
