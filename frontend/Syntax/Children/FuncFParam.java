package Frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.Collections;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class FuncFParam {
    static VarTypes FuncFParamAnalysis() {
        String btype = BType.BTypeAnalysis();
        VarsAttribute va = new VarsAttribute(null);

        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR")) { // ident
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            va.name = token.str;

            if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                va.isArray = true;

                if (!Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                } else {
                    CompUnit.count++;
                }
            }
        }

        if (va.arrSize != 0)
            va.initValues = new ArrayList<>(Collections.nCopies(va.arrSize, 0));
        else {
            va.initValues = new ArrayList<>(Collections.nCopies(1, 0));
        }

        Tools.AddVarSymbol(false, btype, va);

        Tools.WriteLine(Syntax.NodeType.FuncFParam, Tools.GetNowTK().id);

        if (va.isArray) {
            if (btype.equals("Int")) {
                return VarTypes.IntArray;
            } else {
                return VarTypes.CharArray;
            }
        } else {
            if (btype.equals("Int")) {
                return VarTypes.Int;
            } else {
                return VarTypes.Char;
            }
        }
    }
}
