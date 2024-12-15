package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class FuncFParam {
    static VarTypes FuncFParamAnalysis() {
        String btype = BType.BTypeAnalysis();
        VarPart vp = new VarPart(null, false, null, -1);

        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR")) { // ident
            vp.offset = CompUnit.count;
            CompUnit.count++;

            utils.JudgeRepeat(Tools.GetNowTK());

            vp.name = token.str;

            if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                vp.isArray = true;
                // vp.sizeExp = null;
                if (!Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                } else {
                    CompUnit.count++;
                }
            }
        }

        Tools.AddVarSymbol(false, btype, vp);

        Tools.WriteLine(Syntax.NodeType.FuncFParam, Tools.GetNowTK().id);

        if (vp.isArray) {
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
