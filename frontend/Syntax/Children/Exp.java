package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class Exp {
    static void ExpAnalysis() {
        AddExp.AddExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.Exp, Tools.GetNowTK().id);
    }
}
