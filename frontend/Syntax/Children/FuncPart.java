package Frontend.Syntax.Children;

import java.util.ArrayList;

import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class FuncPart {
    String name;
    FuncTypes returnType;
    int paramNumber;
    ArrayList<VarTypes> paramTypes;

    public FuncPart(String name, FuncTypes returnType, int paramNumber, ArrayList<VarTypes> paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramNumber = paramNumber;
        this.paramTypes = paramTypes;
    }
}
