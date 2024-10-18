package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncFParam {
    static void FuncFParamAnalysis() {
        BType.BTypeAnalysis();
        if (Tools.GetNextTK().tk.equals("IDENFR")) {
            if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                
                if (!Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                } else {
                    CompUnit.count++;
                }
            }
        }

        Tools.WriteLine(Syntax.NodeType.FuncFParam, Tools.GetNowTK().id);
    }
}
