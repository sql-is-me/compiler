package frontend.Syntax.Children;

import java.util.ArrayList;

import frontend.Lexer.Lexer.Token;

public class MulExp {
    static void MulExpAnalysis(ArrayList<Token> arr) {
        ArrayList<Token> mularr = new ArrayList<>();

        for (int i = 0; i < arr.size(); i++) {
            Token token = arr.get(i);
            if (!arr.get(i).tk.equals("MULT")
                    && !token.tk.equals("DIV")
                    && !token.tk.equals("MOD")) {
                mularr.add(token);

            } else if (token.tk.equals("MULT")
                    || token.tk.equals("DIV")
                    || token.tk.equals("MOD")) {
                UnaryExp.UnaryExpAnalysis(mularr);
                mularr.clear();
            } else {
                // wrong
            }
        }
    }
}
