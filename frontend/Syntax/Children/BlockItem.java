package Frontend.Syntax.Children;

import Frontend.Lexer.Lexer.Token;

public class BlockItem {
    static void BlockItemAnalysis() {
        Token token = Tools.LookNextTK();
        if (token.tk.equals("INTTK") || token.tk.equals("CHARTK") || token.tk.equals("CONSTTK")) {
            Decl.DeclAnalysis();
        } else {
            Stmt.StmtAnalysis(false);
        }
    }
}
