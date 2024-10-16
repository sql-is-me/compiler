package frontend.Syntax.Children;

public class FuncFParams {
    static void FuncFParamsAnalysis() {
        FuncFParam.FuncFParamAnalysis();
        while (Tools.LookNextTK().tk.equals("COMMA")) {
            FuncFParam.FuncFParamAnalysis();
        }

    }
}
