package Midend.Operands;

import java.util.Stack;

import SymbolTable.VarSymbol;

public class VarOp extends Operands {
    public VarSymbol varSymbol;
    public Operands pos;

    public VarOp(VarSymbol varSymbol,Integer type,Boolean isArray, Operands pos,Stack<Character> opStack) {
        super(type, isArray,opStack);
        this.varSymbol = varSymbol;
        this.pos = pos;
    }
}
