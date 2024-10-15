package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class LVal {
    static void LValAnalysis() {
        Token token = Tools.GetNextTK();
        if (token.tk.equals("LBRACK")) {
            Exp.ExpAnalysis();
            CompUnit.count++; // ]
            Tools.WriteLine(Syntax.NodeType.LVal);
        }
    }
}
