package frontend.Syntax.Children;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (Tools.GetNowTK().tk.equals("INTTK")) { // int
            if (Tools.GetNextTK().tk.equals("MAINTK")) { // main
                if (Tools.GetNextTK().tk.equals("LPARENT")) { // (
                    if (Tools.GetNextTK().tk.equals("RPARENT")) { // )
                        if (Tools.GetNextTK().tk.equals("LBRACE")) { // {
                            Block.BlockAnalysis();
                        } else {
                            // wrong
                        }
                    } else {
                        // wrong
                    }
                } else {
                    // wrong
                }
            } else {
                // wrong
            }
        }
    }
}
