package Frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.Collections;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class ConstDef {
    static VarsAttribute ConstDefAnalysis() {
        VarsAttribute va = new VarsAttribute(null);

        if (Tools.LookNextTK().tk.equals("IDENFR")) {
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            va.name = Tools.GetNowTK().str;

            if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                va.initValues = ConstInitVal.ConstInitValAnalysis(1);
            } else if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++; // [
                va.isArray = true;

                va.arrSize = ConstExp.ConstExpAnalysis();

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                } else {
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                }

                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    va.initValues = ConstInitVal.ConstInitValAnalysis(va.arrSize);
                }
            }

            if (va.initValues == null) {
                if (va.arrSize != 0)
                    va.initValues = new ArrayList<>(Collections.nCopies(va.arrSize, 0));
                else {
                    va.initValues = new ArrayList<>(Collections.nCopies(1, 0));
                }
            }
        }

        Tools.WriteLine(Syntax.NodeType.ConstDef, Tools.GetNowTK().id);

        return va;
    }
}
