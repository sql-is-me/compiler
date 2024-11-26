package SymbolTable;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;

public class VarSymbol extends Symbol {
    public VarTypes type; // ConstInt ConstChar Int Char IntArray CharArray ConstIntArray ConstCharArray
    public ArrayList<Integer> value; // Save value, if not array,just get 0 index
    public int size; // 数组长度
    public ArrayList<Token> valueExp; // 值表达式
    public int registerID; // 寄存器ID

    public VarSymbol(int tableId, String name, VarTypes type, int size, ArrayList<Integer> value,
            ArrayList<Token> valueExp) {
        super(tableId, name);
        this.type = type;
        this.size = size;
        this.value = value;
        this.valueExp = valueExp;
        this.registerID = -1;
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
