package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class VarDef {
    static VarPart VarDefAnalysis() {
        VarPart vp = new VarPart(null, false, null, -1);
        Token token = Tools.LookNextTK();

        if (token.tk.equals("IDENFR")) {
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            vp.name = Tools.GetNowTK().str;
            vp.offset = CompUnit.count;

            token = Tools.LookNextTK();
            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                InitVal.InitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                vp.isArray = true;
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

        return vp;
    }
}
