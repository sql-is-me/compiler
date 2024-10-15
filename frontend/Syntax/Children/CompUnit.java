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
            Token token = Tools.GetNowTK();

            if (token.tk.equals("INTTK") && Tools.GetCountTK(count + 1).tk.equals("MAINTK")
                    && Tools.GetCountTK(count + 2).tk.equals("LPARENT")
                    && Tools.GetCountTK(count + 3).tk.equals("RPARENT")) {
                MainFuncDef.MainFuncDefAnalysis();
                break;
            } else if ((Tools.GetNowTK().tk.equals("INTTK") || Tools.GetNowTK().tk.equals("CHARTK"))
                    && Tools.GetCountTK(CompUnit.count + 2).tk.equals("LPARENT")) {
                FuncDef.FuncDefAnalysis();
            } else {
                Decl.DeclAnalysis();
            }
        }

        Tools.WriteLine(Syntax.NodeType.CompUnit);
    }
}
