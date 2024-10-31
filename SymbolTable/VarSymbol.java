package SymbolTable;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    public VarTypes type; // ConstInt ConstChar Int Char IntArray CharArray ConstIntArray ConstCharArray
    public ArrayList<Integer> value; // Save value, if not array,just get 0 index
    public int size; // array size

    public VarSymbol(int tableId, String name, VarTypes type, int size, ArrayList<Integer> value) {
        super(tableId, name);
        this.type = type;
        this.size = size;
        this.value = value;
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
