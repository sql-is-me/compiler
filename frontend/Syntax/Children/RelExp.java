package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class RelExp {
    static void RelExpAnalysis() {
        AddExp.AddExpAnalysis();
        while (Tools.LookNextTK().tk.equals("LSS") || Tools.LookNextTK().tk.equals("LEQ")
                || Tools.LookNextTK().tk.equals("GRE") || Tools.LookNextTK().tk.equals("GEQ")) {
            Tools.WriteLine(Syntax.NodeType.RelExp, Tools.GetNowTK().id);
            CompUnit.count++; // < <= > >=
            AddExp.AddExpAnalysis();
        }
        Tools.WriteLine(Syntax.NodeType.RelExp, Tools.GetNowTK().id);
    }
}
