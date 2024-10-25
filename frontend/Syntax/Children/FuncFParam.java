package frontend.Syntax.Children;

import SymbolTable.Symbol.TokenType;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncFParam {
    static TokenType FuncFParamAnalysis() {
        String btype = BType.BTypeAnalysis();
        ThreePart tp = new ThreePart(null, false, null);
        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR")) { // ident
            CompUnit.count++;
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

        Tools.AddVarSymbol(btype, tp);

        Tools.WriteLine(Syntax.NodeType.FuncFParam, Tools.GetNowTK().id);

        if (tp.isArray) {
            if (btype.equals("Int")) {
                return TokenType.IntArray;
            } else {
                return TokenType.CharArray;
            }
        } else {
            if (btype.equals("Int")) {
                return TokenType.Int;
            } else {
                return TokenType.Char;
            }
        }
    }
}
