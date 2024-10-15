package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstInitVal {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    static void ConstInitValAnalysis() {
        Token token = Tools.GetNowTK();
        if (token.tk.equals("STRCON")) {
        } else if (token.tk.equals("LBRACE")) { // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                ConstExp.ConstExpAnalysis();
                while (!Tools.GetNowTK().tk.equals("COMMA")) { // ,
                    ConstExp.ConstExpAnalysis();
                }
                ConstExp.ConstExpAnalysis();
            }
        } else {
            CompUnit.count--;
            ConstExp.ConstExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.ConstInitVal);
    }
}