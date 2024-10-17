package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class MulExp {
    static void MulExpAnalysis(int expsize) {

        for (int count = CompUnit.count + 2; count - CompUnit.count < expsize; count += 2) {
            Token token = Tools.GetCountTK(count);
            if (token.tk.equals("MULT")
                    || token.tk.equals("DIV")
                    || token.tk.equals("MOD")) {
                UnaryExp.UnaryExpAnalysis();
                CompUnit.count++; // 踩在* / % 上
            }
        }
        UnaryExp.UnaryExpAnalysis();
    }
}
