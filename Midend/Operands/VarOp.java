package Operands;

import SymbolTable.VarSymbol;

public class VarOp extends Operands {
    public VarSymbol varSymbol;
    public Operands pos;

    public VarOp(VarSymbol varSymbol, Operands pos, boolean needNegative) {
        super(needNegative);
        this.varSymbol = varSymbol;
        this.pos = pos;
    }
}
