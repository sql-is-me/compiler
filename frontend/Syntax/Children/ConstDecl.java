package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstDecl {
    static void ConstDeclAnalysis() {
        Token token = Tools.GetNowTK();

        if (token.tk.equals("CONSTTK")) {
            CompUnit.count++;
            BType.BTypeAnalysis();
            ConstDef.ConstDefAnalysis();

            while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
                CompUnit.count += 2;
                ConstDef.ConstDefAnalysis();
            }
        }

        Tools.WriteLine(Syntax.NodeType.ConstDecl);
    }
}
