package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class MulExp {
    static void MulExpAnalysis() {
        UnaryExp.UnaryExpAnalysis();
        while (Tools.LookNextTK().tk.equals("MULT") || Tools.LookNextTK().tk.equals("DIV")
                || Tools.LookNextTK().tk.equals("MOD")) {
            Tools.WriteLine(Syntax.NodeType.MulExp, Tools.GetNowTK().id);
            CompUnit.count++; // * / %
            UnaryExp.UnaryExpAnalysis();
        }
        Tools.WriteLine(Syntax.NodeType.MulExp, Tools.GetNowTK().id);
    }
}
