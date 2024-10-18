package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class VarDecl {
    static void VarDeclAnalysis() {
        BType.BTypeAnalysis();
        VarDef.VarDefAnalysis();

        while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
            CompUnit.count++;
            VarDef.VarDefAnalysis();
        }

        if (!Tools.LookNextTK().tk.equals("SEMICN")) // 缺;
        {
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'i');
        } else {
            CompUnit.count++;
        }

        Tools.WriteLine(Syntax.NodeType.VarDecl, Tools.GetNowTK().id);
    }
}
