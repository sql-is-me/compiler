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

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;

                } else {
                    // error
                }

                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    InitVal.InitValAnalysis();
                }
            }
        }

        // if (!token.tk.equals("SEMICN")) {
        // ErrorLog.makelog_error(token.line, 'i');
        // CompUnit.count--;
        // }
    }
}
