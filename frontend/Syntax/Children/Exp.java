package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

import java.util.ArrayList;

public class Exp {
    public static ArrayList<Token> exparr = new ArrayList<>();

    static void ExpAnalysis() {
        Token token = UnaryExp.tokenArr.get(UnaryExp.count);
        while (!token.tk.equals("RPARENT")) {
            exparr.add(token);
        }
        AddExp.AddExpAnalysis();
        Tools.WriteLine(Syntax.NodeType.Exp);
    }
}
