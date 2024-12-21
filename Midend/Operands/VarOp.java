package Midend.Operands;

import SymbolTable.VarSymbol;

public class VarOp extends Operands {
    public VarSymbol varSymbol;
    public Operands pos;

    public VarOp(VarSymbol varSymbol,Integer type,Boolean isArray, Operands pos, boolean needNegative, boolean needNot) {
        super(type, isArray, needNegative, needNot);
        this.varSymbol = varSymbol;
        this.pos = pos;
    }
}
