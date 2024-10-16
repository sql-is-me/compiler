package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class RelExp {
    static void RelExpAnalysis(int expsize) {
        int count = CompUnit.count + 1;
        int size = 1;

        for (int i = 1; i < expsize; i += 2, count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("LSS") && !token.tk.equals("LEQ") && !token.tk.equals("GRE")
                    && !token.tk.equals("GEQ")) {
                size += 2;

            } else if (token.tk.equals("LSS") || token.tk.equals("LEQ") || token.tk.equals("GRE")
                    || token.tk.equals("GEQ")) {
                AddExp.AddExpAnalysis(size);
                CompUnit.count++;
                size = 1;
            }
        }
        AddExp.AddExpAnalysis(size);
    }
}
