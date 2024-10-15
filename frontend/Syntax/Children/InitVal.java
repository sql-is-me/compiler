package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class InitVal {
    static void InitValAnalysis() {
        Token token = Tools.GetNowTK();
        if (token.tk.equals("STRCON")) {
        } else if (token.tk.equals("LBRACE")) { // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                Exp.ExpAnalysis();
                while (!Tools.GetNowTK().tk.equals("COMMA")) { // ,
                    Exp.ExpAnalysis();
                }
                Exp.ExpAnalysis();
            }
        } else {
            CompUnit.count--;
            Exp.ExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.ConstInitVal);
    }
}
