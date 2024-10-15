package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class LVal {
    static void LValAnalysis() {
        Token token = UnaryExp.tokenArr.get(UnaryExp.count);
        if (token.tk.equals("IDENFR")) {
            UnaryExp.count++;
            token = UnaryExp.tokenArr.get(UnaryExp.count);
            if (token.tk.equals("LBRACK")) {
                UnaryExp.count++;
                Exp.ExpAnalysis();
            }
            Tools.WriteLine(Syntax.NodeType.LVal);
        }
    }
}
