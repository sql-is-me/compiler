package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class BType {
    static void BTypeAnalysis() {
        Token token = Tools.getToken(CompUnit.count);
        if (token.tk.equals("INTTK") || token.tk.equals("CHARTK")) {
            CompUnit.count++;
        } else {
            // wrong
        }
    }
}