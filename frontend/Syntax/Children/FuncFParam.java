package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class FuncFParam {
    static VarTypes FuncFParamAnalysis() {
        String btype = BType.BTypeAnalysis();
        ThreePart tp = new ThreePart(null, false, null);

        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR")) { // ident
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            tp.name = token.str;

            if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                tp.isArray = true;

                if (!Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                } else {
                    CompUnit.count++;
                }
            }
        }

        Tools.AddVarSymbol(false, btype, tp);

        Tools.WriteLine(Syntax.NodeType.FuncFParam, Tools.GetNowTK().id);

        if (tp.isArray) {
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
