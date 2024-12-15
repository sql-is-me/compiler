package SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

import SymbolTable.VarSymbol.VarTypes;

public class FuncSymbol extends Symbol {
    public FuncTypes returnType; // 函数的返回值类型
    public ArrayList<VarTypes> paramTypes; // 函数的参数类型
    public int paramNumber; // 函数的参数个数
    public int FuncSymTabId; // 函数的符号表id
    public int offset; // 到参数定义结束的')'的位置
    public HashMap<String, Integer> changedGloVar;

    public FuncSymbol(int tableId, String name, FuncTypes returnType, ArrayList<VarTypes> paramTypes,
            int paramNumber, int FuncSymTabId, int offset) {
        super(tableId, name);
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.paramNumber = paramNumber;
        this.FuncSymTabId = FuncSymTabId;
        this.offset = offset;
        changedGloVar = new HashMap<>();
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
