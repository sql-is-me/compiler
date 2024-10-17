package frontend.Syntax.Children;

public class FuncFParam {
    static void FuncFParamAnalysis() {
        BType.BTypeAnalysis();
        if (Tools.GetNextTK().tk.equals("IDENFR")) {
            if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                if (!Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    // error
                } else {
                    CompUnit.count++;
                }
            }
        }
    }
}
