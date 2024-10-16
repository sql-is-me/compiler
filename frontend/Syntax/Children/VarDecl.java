package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class VarDecl {
    static void VarDeclAnalysis() {
        BType.BTypeAnalysis();
        VarDef.VarDefAnalysis();

        while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
            CompUnit.count += 2;
            VarDef.VarDefAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.VarDecl);
    }
}
