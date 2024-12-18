package Midend.Operands;

import SymbolTable.VarSymbol;
import SymbolTable.VarSymbol.VarTypes;

public class VarOp extends Operands {
    public VarSymbol varSymbol;
    public Operands pos;

    public VarOp(VarSymbol varSymbol, Operands pos, boolean needNegative) {
        Integer type = 0;
        Boolean isArray = false;
        if (varSymbol.type.equals(VarTypes.Int) || varSymbol.type.equals(VarTypes.ConstInt)) {
            type = 32;
        } else if (varSymbol.type.equals(VarTypes.Char) || varSymbol.type.equals(VarTypes.ConstChar)) {
            type = 8;
        } else if (varSymbol.type.equals(VarTypes.IntArray) || varSymbol.type.equals(VarTypes.ConstIntArray)) {
            type = 32;
            isArray = true;
        } else if (varSymbol.type.equals(VarTypes.CharArray) || varSymbol.type.equals(VarTypes.ConstCharArray)) {
            type = 8;
            isArray = true;
        }

        super(type, isArray, needNegative);
        this.varSymbol = varSymbol;
        this.pos = pos;
    }
}
