package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class VarDef {
    static void VarDefAnalysis() {
        Token token = Tools.GetNowTK();

        if (token.tk.equals("IDENFR")) {
            token = Tools.GetNextTK();

            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                InitVal.InitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                CompUnit.count++;
                ConstExp.ConstExpAnalysis();

                if (Tools.GetNextTK().tk.equals("RBRACK")) { // ]

                    if (Tools.GetNextTK().tk.equals("ASSIGN")) { // =
                        CompUnit.count++;
                        InitVal.InitValAnalysis();
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

        // if (!token.tk.equals("SEMICN")) {
        // ErrorLog.makelog_error(token.line, 'i');
        // CompUnit.count--;
        // }
    }
}
