package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class VarDecl {
    static void VarDeclAnalysis() {
        String btype = BType.BTypeAnalysis();
        ThreePart tp = VarDef.VarDefAnalysis();

        Tools.AddVarSymbol(false, btype, tp);

        while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
            CompUnit.count++;
            tp = VarDef.VarDefAnalysis();

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
