package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class FuncFParams {
    static void FuncFParamsAnalysis() {
        FuncFParam.FuncFParamAnalysis();
        while (Tools.LookNextTK().tk.equals("COMMA")) {
            CompUnit.count++; // ,
            FuncFParam.FuncFParamAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.FuncFParams, Tools.GetNowTK().id);
    }
}
