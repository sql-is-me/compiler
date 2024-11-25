package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class ForStmt {
    static void ForStmtAnalysis() {
        VarTypes varType = LVal.LValAnalysis();
        utils.JudgeLValisConst(varType, Tools.GetNowTK().line);

        CompUnit.count++; // =

        Exp.ExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.ForStmt, Tools.GetNowTK().id);
    }
}
