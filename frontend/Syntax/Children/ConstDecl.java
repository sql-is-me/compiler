package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.ErrorLog;


public class ConstDecl {
    static void ConstDeclAnalysis() {
        if (Tools.LookNextTK().tk.equals("INTTK") || Tools.LookNextTK().tk.equals("CHARTK")) {
            BType.BTypeAnalysis();
            ConstDef.ConstDefAnalysis();

            while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
                CompUnit.count++;
                ConstDef.ConstDefAnalysis();
            }
        }

        if(!Tools.LookNextTK().tk.equals("SEMICN")) // 缺;
        {
            Token token = Tools.GetNowTK();
            ErrorLog.makelog_error(token.line, 'i');
        }

        Tools.WriteLine(Syntax.NodeType.ConstDecl);
    }
}
