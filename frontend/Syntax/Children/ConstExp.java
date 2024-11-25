package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class ConstExp {
    static void ConstExpAnalysis() {
        AddExp.AddExpAnalysis();
        
        Tools.WriteLine(Syntax.NodeType.ConstExp, Tools.GetNowTK().id);
    }
}
