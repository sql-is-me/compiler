package frontend.Syntax.Children;

public class EqExp {
    static void EqExpAnalysis() {
        AddExp.AddExpAnalysis();
        if (Tools.LookNextTK().tk.equals("EQ") || Tools.LookNextTK().tk.equals("NE")
                || Tools.LookNextTK().tk.equals("LE") || Tools.LookNextTK().tk.equals("GE")
                || Tools.LookNextTK().tk.equals("LT") || Tools.LookNextTK().tk.equals("GT")) {
        }
    }
}
