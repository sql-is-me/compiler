package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class AddExp {
    static void AddExpAnalysis() {
        MulExp.MulExpAnalysis();
        while (Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")) {
            Tools.WriteLine(Syntax.NodeType.AddExp, Tools.GetNowTK().id);
            CompUnit.count++; // +-
            MulExp.MulExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.AddExp, Tools.GetNowTK().id);
    }
}
