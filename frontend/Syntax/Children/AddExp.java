package frontend.Syntax.Children;

import java.util.ArrayList;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class AddExp {
    static void AddExpAnalysis() {
        ArrayList<Token> addarr = new ArrayList<>();

        Token token = Tools.FindNextTk(CompUnit.count);
        while (!Tools.LookNextTk(CompUnit.count).tk.equals("SEMICN")) { // 终止条件存疑 // need fix
            if (!Tools.LookNextTk(CompUnit.count).tk.equals("PLUS")
                    && !Tools.LookNextTk(CompUnit.count).tk.equals("MINU")) {
                addarr.add(token);
            } else if (Tools.LookNextTk(CompUnit.count).tk.equals("PLUS")
                    || Tools.LookNextTk(CompUnit.count).tk.equals("MINU")) {
                MulExp.MulExpAnalysis(addarr);
                addarr.clear();
            } else {
                // wrong
            }
            CompUnit.count++;
        }
    }

    static void AddExpAnalysis(ArrayList<Token> arr) { // Exp版本
        ArrayList<Token> addarr = new ArrayList<>();

        for (Token token : arr) {
            if (!token.tk.equals("PLUS")
                    && !token.tk.equals("MINU")) {
                addarr.add(token);
            } else if (token.tk.equals("PLUS")
                    || token.tk.equals("MINU")) {
                MulExp.MulExpAnalysis(addarr);
                addarr.clear();
            } else {
                // wrong
            }
        }
    }
}
