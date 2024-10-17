package frontend.Syntax.Children;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            Block.BlockAnalysis();
        }
    }
}
