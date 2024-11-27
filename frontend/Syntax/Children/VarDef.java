package Frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.Collections;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class VarDef {
    static VarsAttribute VarDefAnalysis() {
        VarsAttribute va = new VarsAttribute(null);
        Token token = Tools.LookNextTK();

        if (token.tk.equals("IDENFR")) {
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            va.name = Tools.GetNowTK().str;

            token = Tools.LookNextTK();
            if (token.tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                va.valueExp = InitVal.InitValAnalysis();
            } else if (token.tk.equals("LBRACK")) { // [
                va.isArray = true;
                CompUnit.count++;
                va.arrSize = ConstExp.ConstExpAnalysis();

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                } else {
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                }

                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    va.valueExp = InitVal.InitValAnalysis();
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

        Tools.WriteLine(Syntax.NodeType.VarDef, Tools.GetNowTK().id);

        return va;
    }
}
