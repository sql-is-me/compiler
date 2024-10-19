package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class EqExp {
    static void EqExpAnalysis() {
        RelExp.RelExpAnalysis();
        while (Tools.LookNextTK().tk.equals("EQL") || Tools.LookNextTK().tk.equals("NEQ")) {
            Tools.WriteLine(Syntax.NodeType.EqExp, Tools.GetNowTK().id);
            CompUnit.count++; // == !=
            RelExp.RelExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.EqExp, Tools.GetNowTK().id);
    }
}
