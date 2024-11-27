package SymbolTable;

import java.util.ArrayList;

import SymbolTable.VarSymbol.VarTypes;

public class FuncSymbol extends Symbol {
    public FuncTypes returnType; // 函数的返回值类型
    public ArrayList<VarTypes> paramTypes; // 函数的参数类型
    public int paramNumber; // 函数的参数个数
    public int symTabID; // 函数的符号表ID
    /** 在token集合中定义的位置 */
    public int offset;

    public FuncSymbol(int tableId, String name, FuncTypes returnType, ArrayList<VarTypes> paramTypes,
            int paramNumber, int symTabID, int offset) {
        super(tableId, name);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramNumber = paramNumber;
        this.symTabID = symTabID;
        this.offset = offset;
    }

    public enum FuncTypes {
        IntFunc,
        CharFunc,
        VoidFunc,
        Undefined
    }

    @Override
    public String toString() {
        return " " + name + " " + returnType;
    }
}
