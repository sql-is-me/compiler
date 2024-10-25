package frontend.Syntax.Children;

import java.util.ArrayList;

import frontend.Lexer.Lexer.Token;

public class ThreePart {
    String name;
    Boolean isArray;
    ArrayList<Token> sizeExp;

    public ThreePart(String name, boolean isArray, ArrayList<Token> Exp) {
        this.name = name;
        this.isArray = isArray;
        this.sizeExp = Exp;
    }
}
