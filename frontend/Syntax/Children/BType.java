package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class BType {
    static void BTypeAnalysis() {
        Token token = Tools.GetNowTK();
        if (token.tk.equals("INTTK") || token.tk.equals("CHARTK")) {
            CompUnit.count++;
        } else {
            // wrong
        }
    }
}