package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class LVal {
    static void LValAnalysis() {
        Token token = Tools.GetNextTK();
        if (token.tk.equals("LBRACK")) { // [
            Exp.ExpAnalysis();
            if (!Tools.LookNextTK().tk.equals("LBRACK")) { // 缺 ]
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'k');
            } else {
                CompUnit.count++; // ]
            }
        }
        Tools.WriteLine(Syntax.NodeType.LVal, Tools.GetNowTK().id);
    }
}
