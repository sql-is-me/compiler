package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstDecl {
    static void ConstDeclAnalysis() {
        Token token = Tools.getToken(CompUnit.count);

        if (token.tk.equals("CONSTTK")) {
            CompUnit.count++;
            BType.BTypeAnalysis();
            ConstDef.ConstDefAnalysis();

            while (Tools.getToken(CompUnit.count + 1).tk.equals("COMMA")) { // "," 多个参数
                CompUnit.count += 2;
                ConstDef.ConstDefAnalysis();
            }
        } else {

        }

        Tools.WriteLine(Syntax.NodeType.ConstDecl);
    }
}
