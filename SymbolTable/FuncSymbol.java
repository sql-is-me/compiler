package SymbolTable;

import java.util.ArrayList;

import SymbolTable.VarSymbol.VarTypes;

public class FuncSymbol extends Symbol {
    public FuncTypes returnType; // 函数的返回值类型
    public ArrayList<VarTypes> paramTypes; // 函数的参数类型
    public int paramNumber; // 函数的参数个数

    public FuncSymbol(int tableId, String name, FuncTypes returnType, ArrayList<VarTypes> paramTypes,
            int paramNumber) {
        super(tableId, name);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramNumber = paramNumber;
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
