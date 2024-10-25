package frontend.Syntax.Children;

import java.util.ArrayList;

import SymbolTable.Symbol.TokenType;

public class FuncPart {
    String name;
    int paramCount;
    ArrayList<TokenType> paramTypes;

    public FuncPart(String name, int paramCount, ArrayList<TokenType> paramTypes) {
        this.name = name;
        this.paramCount = paramCount;
        this.paramTypes = paramTypes;
    }
}
