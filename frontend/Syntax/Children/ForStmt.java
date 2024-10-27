package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class ForStmt {
    static void ForStmtAnalysis() {
        LVal.LValAnalysis();
        CompUnit.count++; // =
        Exp.ExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.ForStmt, Tools.GetNowTK().id);
    }
}
