package Frontend.Syntax;

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

import Frontend.Lexer.Lexer.Token;

public class utils {
    public static Queue<ArrayList<Token>> exps = new LinkedList<>();

    public static void addExp(ArrayList<Token> exp) {
        exps.add(exp);
    }

    public static ArrayList<Token> getExp() {
        return exps.poll();
    }
}
