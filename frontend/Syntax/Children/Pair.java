package frontend.Syntax.Children;

import java.util.ArrayList;

import frontend.Lexer.Lexer.Token;

public class Pair {
    String name;
    ArrayList<Token> sizeExp;

    public Pair(String name, ArrayList<Token> Exp) {
        this.name = name;
        this.sizeExp = Exp;
    }
}
