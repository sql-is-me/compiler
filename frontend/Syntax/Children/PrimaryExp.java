package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class PrimaryExp {
    static void PrimaryExpAnalysis() {
        Token token = Tools.LookNextTK();

        if (token.tk.equals("LPARENT")) { // (
            Exp.ExpAnalysis();
            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺 )
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'j');
            } else {
                CompUnit.count++; // )
            }
        } else if (token.tk.equals("INTCON")) {
            Num_Char.NumberAnalysis();
        } else if (token.tk.equals("CHARCON")) {
            Num_Char.CharacterAnalysis();
        } else if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
        }
    }
}
