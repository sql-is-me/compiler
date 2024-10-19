package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class LOrExp {

    public static void LOrExpAnalysis() {
        LAndExp.LAndExpAnalysis();
        while (Tools.LookNextTK().tk.equals("OR")) {
            Tools.WriteLine(Syntax.NodeType.LOrExp, Tools.GetNowTK().id);
            CompUnit.count++; // ||
            LAndExp.LAndExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.LOrExp, Tools.GetNowTK().id);

    }
}
