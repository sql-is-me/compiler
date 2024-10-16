package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class MulExp {
    static void MulExpAnalysis(int expsize) {
        int count = CompUnit.count + 2;

        for (int i = 1; i < expsize; i += 2, count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("MULT")
                    && !token.tk.equals("DIV")
                    && !token.tk.equals("MOD")) {

            } else if (token.tk.equals("MULT")
                    || token.tk.equals("DIV")
                    || token.tk.equals("MOD")) {
                UnaryExp.UnaryExpAnalysis();
                CompUnit.count++;
            }
        }
        UnaryExp.UnaryExpAnalysis();
    }
}
