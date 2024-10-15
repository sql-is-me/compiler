package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class UnaryExp {
    static void UnaryExpAnalysis() {
        Token token = Tools.GetNextTK();
        if (token.tk.equals("LPARENT") || token.tk.equals("INTCON")
                || token.tk.equals("CHRCON")) { // (,number,character
            PrimaryExp.PrimaryExpAnalysis();
        } else if (token.tk.equals("IDENFR")) {
            token = Tools.LookNextTK();
            if (token.tk.equals("LPARENT")) {
                // FuncRParams

            } else { // LVal
                PrimaryExp.PrimaryExpAnalysis();
            }
        } else if (token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("NOT")) {
            UnaryOp.UnaryOpAnalysis();
            UnaryExpAnalysis();
        }
    }
}
