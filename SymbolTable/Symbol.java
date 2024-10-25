package SymbolTable;

import java.util.ArrayList;

public class Symbol {
    public int tableId; // 符号表id
    public int id; // 表内token的id
    public String name; // 名
    public TokenType type;// ConstInt ConstChar Int Char VoidFunc IntFunc CharFunc IntArray CharArray
                          // ConstIntArray ConstCharArray
    public ArrayList<Integer> value; // Save value, if not array,just get 0 index
    public int size; // array size
    public ArrayList<TokenType> funcParaTypes; // 函数的参数类型

    public enum TokenType {
        ConstInt,
        ConstChar,
        Int,
        Char,
        IntFunc,
        CharFunc,
        VoidFunc,
        IntArray,
        CharArray,
        ConstIntArray,
        ConstCharArray
    }

    private static int idcount = 0;

    public Symbol(int tableId, String name, TokenType type, ArrayList<Integer> value, int size,
            ArrayList<TokenType> funcParaTypes) {
        this.tableId = tableId;
        this.id = idcount++;
        this.name = name;
        this.type = type;
        this.value = value;
        this.size = size;
        this.funcParaTypes = funcParaTypes;
    }

    @Override
    public String toString() {
        return " " + name + " " + type;
    }
}
