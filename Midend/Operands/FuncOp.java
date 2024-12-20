package Midend.Operands;

import java.util.ArrayList;

import SymbolTable.FuncSymbol;
import SymbolTable.FuncSymbol.FuncTypes;

public class FuncOp extends Operands {
    public FuncSymbol funcSymbol;
    public ArrayList<Operands> params;

    public FuncOp(FuncSymbol funcSymbol, Integer type, ArrayList<Operands> params, boolean needNegative,
            boolean needNot) {
        super(type, false, needNegative, needNot);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }
}
