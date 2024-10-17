package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class UnaryExp {
    static void UnaryExpAnalysis() {
        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR") && Tools.GetCountTK(CompUnit.count + 2).tk.equals("LPARENT")) { // ident (
            CompUnit.count += 2;
            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                FuncRParams.FuncRParamsAnalysis();
            }
            CompUnit.count++; // )

        } else if (token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("NOT")) {
            UnaryOp.UnaryOpAnalysis();
            UnaryExpAnalysis();
            
        } else if (token.tk.equals("LPARENT") || token.tk.equals("INTCON")
                || token.tk.equals("CHRCON") || token.tk.equals("IDENFR")) { // (,number,character,ident
            PrimaryExp.PrimaryExpAnalysis();
        }
    }
}
