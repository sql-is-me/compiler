package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class EqExp {
    static void EqExpAnalysis(int expsize) {
        int count = CompUnit.count + 1;
        int size = 1;

        for (int i = 1; i < expsize; i += 2, count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("EQL") && !token.tk.equals("NEQ")) {
                size += 2;

            } else if (token.tk.equals("EQL") || token.tk.equals("NEQ")) {
                RelExp.RelExpAnalysis(size);
                CompUnit.count++;
                size = 1;
            }
        }
        RelExp.RelExpAnalysis(size);
    }
}
