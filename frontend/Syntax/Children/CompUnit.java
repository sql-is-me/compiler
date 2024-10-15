package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Lexer.Lexer;

import java.util.List;

public class CompUnit {
    public static int count = 0;
    public static List<Token> words = Lexer.tokens;
    private static int size = words.size();

    public static void CompUnitAnalysis() {
        while (count < size) {
            Token token = Tools.getToken(count);

            if (token.tk.equals("CONSTTK")) {
                Decl.DeclAnalysis();
            } else if (token.tk.equals("INTTK") && Tools.getToken(count + 1).tk.equals("MAINTK")
                    && Tools.getToken(count + 2).tk.equals("LPARENT") && Tools.getToken(count + 3).tk.equals("RPARENT")) {
                MainFuncDef.MainFuncDefAnalysis();
            }
        }

        Tools.WriteLine(Syntax.NodeType.CompUnit);
    }
}
