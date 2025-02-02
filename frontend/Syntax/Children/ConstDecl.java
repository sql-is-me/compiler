package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class ConstDecl {
    static void ConstDeclAnalysis() {
        CompUnit.count++; // const

        if (Tools.LookNextTK().tk.equals("INTTK") || Tools.LookNextTK().tk.equals("CHARTK")) {
            String btype = BType.BTypeAnalysis();
            VarsAttribute va = ConstDef.ConstDefAnalysis();

            Tools.AddVarSymbol(true, btype, va);

            while (Tools.LookNextTK().tk.equals("COMMA")) { // "," 多个参数
                CompUnit.count++;
                va = ConstDef.ConstDefAnalysis();
                
                Tools.AddVarSymbol(true, btype, va);
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
