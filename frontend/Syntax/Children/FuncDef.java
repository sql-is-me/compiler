package frontend.Syntax.Children;

public class FuncDef {
    static void FuncDefAnalysis() {
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
            FuncFParams.FuncFParamsAnalysis();
        }
        CompUnit.count++; // )
        Block.BlockAnalysis();
    }

    // token = Tools.GetNextTK(CompUnit.count);
    // if (!token.tk.equals("SEMICN")) {
    // ErrorLog.makelog_error(token.line, 'i');
    // CompUnit.count--;
    // }

}
