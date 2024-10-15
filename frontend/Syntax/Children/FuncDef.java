package frontend.Syntax.Children;

public class FuncDef {
    static void FuncDefAnalysis() {
        if ((Tools.GetNowTK().tk.equals("INTTK") || Tools.GetNowTK().tk.equals("CHARTK"))) {
            if (Tools.GetNextTK().tk.equals("IDENFR")) {
                if (Tools.GetNextTK().tk.equals("LPARENT")) {
                    if (!Tools.LookNextTK().tk.equals("RPARENT")) {
                        //
                    }
                    CompUnit.count += 2; // ) {
                    Block.BlockAnalysis();
                }
            }
        } else {
            // erorr
        }

        // token = Tools.GetNextTK(CompUnit.count);
        // if (!token.tk.equals("SEMICN")) {
        // ErrorLog.makelog_error(token.line, 'i');
        // CompUnit.count--;
        // }

    }
}
