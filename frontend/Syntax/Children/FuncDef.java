package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncDef {
    static void FuncDefAnalysis() {
        FuncType.FuncTypeAnalysis();
        CompUnit.count += 2; // IDENFR (

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

        Tools.WriteLine(Syntax.NodeType.FuncDef, Tools.GetNowTK().id);
    }
}
