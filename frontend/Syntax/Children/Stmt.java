package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class Stmt {
    static void StmtAnalysis() {
        Token token = UnaryExp.tokenArr.get(UnaryExp.count);

        if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
            CompUnit.count++;
            Exp.ExpAnalysis();;
        } else if (token.tk.equals("INTCON")) {
            Num_Char.NumberAnalysis();
        } else if (token.tk.equals("CHARCON")) {
            Num_Char.CharacterAnalysis();
        } else if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
        } else {
            // wrong
        }
    }
}
