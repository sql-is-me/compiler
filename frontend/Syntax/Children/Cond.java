package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class Cond {
    static void CondAnalysis() {
        LOrExp.LOrExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.Cond, Tools.GetNowTK().id);
    }
}
