package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstDef {
    static ThreePart ConstDefAnalysis() {
        ThreePart tp = new ThreePart(null, false, null);

        if (Tools.LookNextTK().tk.equals("IDENFR")) {
            CompUnit.count++;

            if (utils.JudgeRepeat(Tools.GetNowTK())) {
                // make errorlog
            }

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
