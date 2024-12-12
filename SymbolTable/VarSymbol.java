package SymbolTable;

public class VarSymbol extends Symbol {
    public VarTypes type; // ConstInt ConstChar Int Char IntArray CharArray ConstIntArray ConstCharArray
    public int size; // array size
    public int offset; // 相对于当前符号表的偏移量

    public VarSymbol(int tableId, String name, VarTypes type, int size, int offset) {
        super(tableId, name);
        this.type = type;
        this.size = size;
        this.offset = offset;
    }

    public enum VarTypes {
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

    @Override
    public String toString() {
        return " " + name + " " + type;
    }
}
