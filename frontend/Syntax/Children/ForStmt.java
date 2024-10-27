package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.Syntax.Syntax;

public class ForStmt {
    static void ForStmtAnalysis() {
        String varType = LVal.LValAnalysis();
        utils.JudgeLValisConst(varType, Tools.GetNowTK().line);

        CompUnit.count++; // =

        Exp.ExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.ForStmt, Tools.GetNowTK().id);
    }
}
