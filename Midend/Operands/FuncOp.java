package Midend.Operands;

import java.util.ArrayList;

import SymbolTable.FuncSymbol;
import SymbolTable.FuncSymbol.FuncTypes;

public class FuncOp extends Operands {
    public FuncSymbol funcSymbol;
    public ArrayList<Operands> params;

    public FuncOp(FuncSymbol funcSymbol, ArrayList<Operands> params, boolean needNegative,boolean needNot) {
        Integer type = 0;
        if (funcSymbol.returnType.equals(FuncTypes.IntFunc)) {
            type = 32;
        } else {
            type = 8;
        }
        super(type, false, needNegative,needNot);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }
}
