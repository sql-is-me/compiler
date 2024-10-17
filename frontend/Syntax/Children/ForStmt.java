package frontend.Syntax.Children;

public class ForStmt {
    static void ForStmtAnalysis() {
        LVal.LValAnalysis();
        if (Tools.LookNextTK().tk.equals("ASSIGN")) {
            CompUnit.count++; // =
            Exp.ExpAnalysis();
        }
    }
}
