package frontend.Syntax.Children;

import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;
import frontend.Syntax.Syntax;

public class ForStmt {
    static void ForStmtAnalysis() {
        VarTypes varType = LVal.LValAnalysis();
        utils.JudgeLValisConst(varType, Tools.GetNowTK().line);

        CompUnit.count++; // =

        Exp.ExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.ForStmt, Tools.GetNowTK().id);
    }
}
