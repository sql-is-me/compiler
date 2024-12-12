package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;

public class VarPart {
    String name;
    Boolean isArray;
    ArrayList<Token> sizeExp;
    int offset;

    public VarPart(String name, boolean isArray, ArrayList<Token> Exp, int offset) {
        this.name = name;
        this.isArray = isArray;
        this.sizeExp = Exp;
        this.offset = offset;
    }
}
