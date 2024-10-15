package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class Decl {
    static void DeclAnalysis() {
        Token token = Tools.GetNowTK();

        if (token.tk.equals("CONSTTK")) {
            ConstDecl.ConstDeclAnalysis();
        } else if (token.tk.equals("INTTK") || token.tk.equals("CHARTK")) {
            VarDecl.VarDeclAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.Decl);
    }
}