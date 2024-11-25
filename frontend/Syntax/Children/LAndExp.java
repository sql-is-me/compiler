package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class LAndExp {
    static void LAndExpAnalysis() {
        EqExp.EqExpAnalysis();
        while (Tools.LookNextTK().tk.equals("AND")) {
            Tools.WriteLine(Syntax.NodeType.LAndExp, Tools.GetNowTK().id);
            CompUnit.count++; // &&
            EqExp.EqExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.LAndExp, Tools.GetNowTK().id);

    }
}
