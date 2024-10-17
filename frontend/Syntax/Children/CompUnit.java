package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Lexer.Lexer;

import java.util.List;

public class CompUnit {
    public static int count = 0;
    public static List<Token> words = Lexer.tokens;

    public static void CompUnitAnalysis() {
        Token token;
        while (true) {
            token = Tools.GetNowTK();
            if ((token.tk.equals("INTTK") || token.tk.equals("CHARTK")
                    || token.tk.equals("VOIDTK"))
                    && Tools.GetCountTK(CompUnit.count + 1).tk.equals("IDENFR")
                    && Tools.GetCountTK(CompUnit.count + 2).tk.equals("LPARENT")) { // Type ident (

                CompUnit.count += 2; // 回到上一位
                FuncDef.FuncDefAnalysis();
            } else if ((token.tk.equals("INTTK") || token.tk.equals("CHARTK"))
                    && Tools.GetCountTK(CompUnit.count + 1).tk.equals("IDENFR")) { // Type ident

                CompUnit.count--;
                Decl.DeclAnalysis();
            } else if (token.tk.equals("CONSTTK")) { // const
                CompUnit.count--;
                Decl.DeclAnalysis();
            } else { //
                break;
            }
        }

        CompUnit.count--;
        if (Tools.LookNextTK().tk.equals("INTTK") && Tools.GetCountTK(count + 2).tk.equals("MAINTK")
                && Tools.GetCountTK(count + 3).tk.equals("LPARENT")) {
            CompUnit.count += 3; // (
            MainFuncDef.MainFuncDefAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.CompUnit);
    }
}
