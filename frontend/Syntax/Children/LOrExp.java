package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class LOrExp {
    static void LOrExpAnalysis() {
        int count = CompUnit.count + 1;
        int size = 1;
        Token token = Tools.GetCountTK(count);
        while (token.tk.equals("AND") || token.tk.equals("OR") || token.tk.equals("EQL") || token.tk.equals("NEQ")
                || token.tk.equals("LSS") || token.tk.equals("LEQ") || token.tk.equals("GRE") || token.tk.equals("GEQ")
                || token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("MULT")
                || token.tk.equals("DIV") || token.tk.equals("MOD")) {
            // && || == != < <= > >= + - * / %

            if (!token.tk.equals("OR")) {
                size += 2;
            } else if (token.tk.equals("OR")) {
                LAndExp.LAndExpAnalysis(size);
                CompUnit.count++;
                size = 1;
            }
            count += 2;
        }
        LAndExp.LAndExpAnalysis(size);
    }

}
