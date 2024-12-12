package Operands;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import SymbolTable.FuncSymbol;

public class FuncOp extends Operands {
    FuncSymbol funcSymbol;
    ArrayList<ArrayList<Token>> params;

    public FuncOp(FuncSymbol funcSymbol, ArrayList<ArrayList<Token>> params, boolean needNegative) {
        super(needNegative);
        this.funcSymbol = funcSymbol;
        this.params = params;
    }
}
