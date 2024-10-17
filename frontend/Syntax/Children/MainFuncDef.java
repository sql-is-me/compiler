package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }
        
        if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            Block.BlockAnalysis();
        }
    }
}
