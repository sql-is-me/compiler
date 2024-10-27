package SymbolTable;

public class Symbol {
    public int tableId; // 符号表id
    public int id; // 表内token的id
    public String name; // 名

    public Symbol(int tableId, String name) {
        this.tableId = tableId;
        this.id = ++utils.idCount_Symbol;
        this.name = name;
    }

    /** 启动遍历输出 */
    public static void VisitAllSymbolTable() {
        utils.VisitAllSymTabs(utils.globalSymTab);
    }
}
