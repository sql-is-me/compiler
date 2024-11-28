package SymbolTable;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;

public class VarSymbol extends Symbol {
    public VarTypes type; // ConstInt ConstChar Int Char IntArray CharArray ConstIntArray ConstCharArray
    public ArrayList<Integer> value; // Save value, if not array,just get 0 index
    public int size; // 数组长度
    public ArrayList<ArrayList<Token>> valueExp; // 值表达式
    public boolean zeroinitializer;
    public int stackRegID; // 栈寄存器ID
    public int valueRegID; // 值寄存器ID
    public boolean needAssignVReg; // 当栈寄存器中存储的值发生改变时，将其置为true。在重新分配完值寄存器ID后，将其置为false。

    public VarSymbol(int tableId, String name, VarTypes type, int size, ArrayList<Integer> value,
            ArrayList<ArrayList<Token>> valueExp, boolean zeroinitializer) {
        super(tableId, name);
        this.type = type;
        this.size = size;
        this.value = value;
        this.valueExp = valueExp;
        this.zeroinitializer = zeroinitializer;
        this.stackRegID = -1;
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
