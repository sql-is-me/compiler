package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class ConstExp {
    static void ConstExpAnalysis() {
        AddExp.AddExpAnalysis();
        Tools.WriteLine(Syntax.NodeType.ConstExp);
    }
}
