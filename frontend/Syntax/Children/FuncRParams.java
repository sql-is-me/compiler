package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class FuncRParams {
    static void FuncRParamsAnalysis() {
        Exp.ExpAnalysis();
        while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
            CompUnit.count++;
            Exp.ExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.FuncRParams, Tools.GetNowTK().id);
    }

}
