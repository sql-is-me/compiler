package Frontend.Syntax.Children;

import Frontend.Lexer.Lexer.Token;

public class Decl {
    static void DeclAnalysis() {
        Token token = Tools.LookNextTK();

        if (token.tk.equals("CONSTTK")) {
            ConstDecl.ConstDeclAnalysis();
        } else if (token.tk.equals("INTTK") || token.tk.equals("CHARTK")) {
            VarDecl.VarDeclAnalysis();
        }

        // Tools.WriteLine(Syntax.NodeType.Decl, Tools.GetNowTK().id);
    }
}