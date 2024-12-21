package Midend.Operands;

import java.util.ArrayList;
import java.util.Stack;

import SymbolTable.FuncSymbol;

public class FuncOp extends Operands {
    public FuncSymbol funcSymbol;
    public ArrayList<Operands> params;

    public FuncOp(FuncSymbol funcSymbol, Integer type, ArrayList<Operands> params, Stack<Character> opStack) {
        super(type, false, opStack);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }
}
