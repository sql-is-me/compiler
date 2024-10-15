package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class ConstDef {
    static void ConstDefAnalysis() {
        Token token = Tools.FindNextTk(CompUnit.count);

        if (token.tk.equals("IDENFR")) {
            token = Tools.FindNextTk(CompUnit.count);

            if (token.tk.equals("ASSIGN")) { // =
                ConstInitVal.ConstInitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                ConstExp.ConstExpAnalysis();

                if (!Tools.FindNextTk(CompUnit.count).tk.equals("RBRACK")) { // ]

                    if (token.tk.equals("ASSIGN")) { // =
                        ConstInitVal.ConstInitValAnalysis();
                    }
                } else {
                    // error
                }
            } else {
                // wrong
            }
        } else {
            // erorr
        }

        token = Tools.FindNextTk(CompUnit.count);
        if (!token.tk.equals("SEMICN")) {
            ErrorLog.makelog_error(token.line, 'i');
            CompUnit.count--;
        }

    }
}
