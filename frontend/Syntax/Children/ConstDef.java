package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class ConstDef {
    static ThreePart ConstDefAnalysis() {
        ThreePart tp = new ThreePart(null, false, null);

        if (Tools.LookNextTK().tk.equals("IDENFR")) {
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            tp.name = Tools.GetNowTK().str;

            if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                ConstInitVal.ConstInitValAnalysis();
            } else if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++; // [
                tp.isArray = true;

                ConstExp.ConstExpAnalysis();

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                } else {
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                }

                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    ConstInitVal.ConstInitValAnalysis();
                }
            }
        }

        Tools.WriteLine(Syntax.NodeType.ConstDef, Tools.GetNowTK().id);

        return tp;
    }
}
