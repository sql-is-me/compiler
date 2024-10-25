package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.ErrorLog;

public class ConstDecl {
    static void ConstDeclAnalysis() {
        CompUnit.count++; // const

        if (Tools.LookNextTK().tk.equals("INTTK") || Tools.LookNextTK().tk.equals("CHARTK")) {
            String btype = BType.BTypeAnalysis();
            ThreePart tp = ConstDef.ConstDefAnalysis();

            Tools.AddConstSymbol(btype, tp);

            while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
                CompUnit.count++;
                tp = ConstDef.ConstDefAnalysis();

                Tools.AddConstSymbol(btype, tp);
            }
        }

        if (!Tools.LookNextTK().tk.equals("SEMICN")) // 缺;
        {
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'i');
        } else {
            CompUnit.count++; // ;
        }

        Tools.WriteLine(Syntax.NodeType.ConstDecl, Tools.GetNowTK().id);
    }
}
