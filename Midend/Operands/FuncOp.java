package Operands;

import java.util.ArrayList;

import SymbolTable.FuncSymbol;
import SymbolTable.FuncSymbol.FuncTypes;

public class FuncOp extends Operands {
    public FuncSymbol funcSymbol;
    public ArrayList<Operands> params;

    public FuncOp(FuncSymbol funcSymbol, ArrayList<Operands> params, boolean needNegative) {
        Integer type = 0;
        if (funcSymbol.returnType.equals(FuncTypes.IntFunc)) {
            type = 32;
        } else {
            type = 8;
        }
        super(type, false, needNegative);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }
}
