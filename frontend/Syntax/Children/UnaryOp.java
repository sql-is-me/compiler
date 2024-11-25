package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class UnaryOp {
    static void UnaryOpAnalysis() {
        CompUnit.count++; // + - !

        Tools.WriteLine(Syntax.NodeType.UnaryOp, Tools.GetNowTK().id);
    }
}
