package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class UnaryExp {
    static void UnaryExpAnalysis() {
        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR") && Tools.GetCountTK(CompUnit.count + 2).tk.equals("LPARENT")) {
            CompUnit.count++;// ident
            
            utils.JudgeUndefined(Tools.GetNowTK());

            CompUnit.count++; // (
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                FuncRParams.FuncRParamsAnalysis();
            }
            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'j');
            } else {
                CompUnit.count++; // )
            }

        } else if (token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("NOT")) {
            UnaryOp.UnaryOpAnalysis();
            UnaryExpAnalysis();

        } else if (token.tk.equals("LPARENT") || token.tk.equals("INTCON")
                || token.tk.equals("CHRCON") || token.tk.equals("IDENFR")) { // (,number,character,ident
            PrimaryExp.PrimaryExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.UnaryExp, Tools.GetNowTK().id);
    }
}
