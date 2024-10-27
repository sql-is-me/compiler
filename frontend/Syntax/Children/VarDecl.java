package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class VarDecl {
    static void VarDeclAnalysis() {
        String btype = BType.BTypeAnalysis();
        ThreePart tp = VarDef.VarDefAnalysis();

        Tools.AddVarSymbol(false, btype, tp);

        while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
            CompUnit.count++;
            VarDef.VarDefAnalysis();

            Tools.AddVarSymbol(false, btype, tp);
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
