package frontend.Syntax.Children;

public class FuncRParams {
    static void FuncRParamsAnalysis() {
        Exp.ExpAnalysis();
        while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
            CompUnit.count++;
            Exp.ExpAnalysis();
        }
    }

}
