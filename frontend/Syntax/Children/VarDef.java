package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class VarDef {
    static void VarDefAnalysis() {
        Token token = Tools.LookNextTK();

        if (token.tk.equals("IDENFR")) {
            CompUnit.count++;
            token = Tools.LookNextTK();

            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                InitVal.InitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                CompUnit.count++;
                ConstExp.ConstExpAnalysis();

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                } else {
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                }

                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    InitVal.InitValAnalysis();
                }
            }
        }

        Tools.WriteLine(Syntax.NodeType.VarDef, Tools.GetNowTK().id);
    }
}
