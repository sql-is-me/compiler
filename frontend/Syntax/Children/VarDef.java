package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class VarDef {
    static ThreePart VarDefAnalysis() {
        ThreePart tp = new ThreePart(null, false, null);
        Token token = Tools.LookNextTK();

        if (token.tk.equals("IDENFR")) {
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            tp.name = Tools.GetNowTK().str;

            token = Tools.LookNextTK();
            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                InitVal.InitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                tp.isArray = true;
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

        return tp;
    }
}
