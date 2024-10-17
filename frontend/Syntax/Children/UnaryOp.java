package frontend.Syntax.Children;

import frontend.Syntax.Syntax;


public class UnaryOp {
    static void UnaryOpAnalysis()
    {
        CompUnit.count++;
        Tools.WriteLine(Syntax.NodeType.UnaryOp);
    }
}
