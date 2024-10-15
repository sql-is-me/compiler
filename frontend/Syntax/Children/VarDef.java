package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class VarDef {
    static void VarDefAnalysis() {
        Token token = Tools.FindNextTk(CompUnit.count);

        if (token.tk.equals("IDENFR")) {
            token = Tools.FindNextTk(CompUnit.count);

            if (token.tk.equals("ASSIGN")) { // =
                AddExp.AddExpAnalysis();
            } else if (token.tk.equals("LBRACK")) {

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
