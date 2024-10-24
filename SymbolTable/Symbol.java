package SymbolTable;

public class Symbol {
    public int tableId; // 符号表id
    public int id; // 表内token的id
    public String name; // 名
    public TokenType type;// ConstInt ConstChar Int Char IntFunc CharFunc IntArray CharArray
    public int btype; // 0: char 1: int
    public boolean isConst; // true: Const false: Var

    public enum TokenType {
        ConstInt,
        ConstChar,
        Int,
        Char,
        IntFunc,
        CharFunc,
        IntArray,
        CharArray
    }

    private static int idcount = 0;

    public Symbol(int tableId, String name, TokenType type, int btype, boolean isConst) {
        this.tableId = tableId;
        this.id = idcount++;
        this.name = name;
        this.type = type;
        this.btype = btype;
        this.isConst = isConst;
    }

    @Override
    public String toString() {
        return " " + name + " " + type;
    }
}
