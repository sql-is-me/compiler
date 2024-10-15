package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class ConstDef {
    static void ConstDefAnalysis() {
        Token token = Tools.GetNowTK();

        if (token.tk.equals("IDENFR")) {
            token = Tools.GetNextTK();

            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                ConstInitVal.ConstInitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                CompUnit.count++;
                ConstExp.ConstExpAnalysis();

                if (Tools.GetNextTK().tk.equals("RBRACK")) { // ]

                    if (Tools.GetNextTK().tk.equals("ASSIGN")) { // =
                        CompUnit.count++;
                        ConstInitVal.ConstInitValAnalysis();
                    } else {
                        // wrong
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

        // token = Tools.GetNextTK(CompUnit.count);
        // if (!token.tk.equals("SEMICN")) {
        // ErrorLog.makelog_error(token.line, 'i');
        // CompUnit.count--;
        // }

    }
}
