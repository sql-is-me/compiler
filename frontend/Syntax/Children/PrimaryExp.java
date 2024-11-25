package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class PrimaryExp {
    static void PrimaryExpAnalysis() {
        Token token = Tools.LookNextTK();

        if (token.tk.equals("LPARENT")) { // (
            CompUnit.count++; // (
            Exp.ExpAnalysis();
            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺 )
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'j');
            } else {
                CompUnit.count++; // )
            }
        } else if (token.tk.equals("INTCON")) {
            Num_Char.NumberAnalysis();
        } else if (token.tk.equals("CHRCON")) {
            Num_Char.CharacterAnalysis();
        } else if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.PrimaryExp, Tools.GetNowTK().id);
    }
}
