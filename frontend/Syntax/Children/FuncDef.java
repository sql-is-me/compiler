package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class FuncDef {
    static void FuncDefAnalysis() {
        if (!Tools.LookNextTK().tk.equals("RPARENT") && !Tools.LookNextTK().tk.equals("LBRACE")) { // ) {
            FuncFParams.FuncFParamsAnalysis();
        }
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }
        Block.BlockAnalysis();
    }
}
