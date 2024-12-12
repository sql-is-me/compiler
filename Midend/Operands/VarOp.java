package Operands;

import SymbolTable.VarSymbol;

public class VarOp extends Operands {
    VarSymbol varSymbol;

    public VarOp(VarSymbol varSymbol, boolean needNegative) {
        super(needNegative);
        this.varSymbol = varSymbol;
    }
}
