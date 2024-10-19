package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class InitVal {
    static void InitValAnalysis() {
        if (Tools.LookNextTK().tk.equals("STRCON")) {
            CompUnit.count++;
        } else if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            CompUnit.count++; // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                Exp.ExpAnalysis();
                while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
                    CompUnit.count++;
                    Exp.ExpAnalysis();
                }
                CompUnit.count++; // }
            } else {
                CompUnit.count++;
            }
        } else {
            Exp.ExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.InitVal, Tools.GetNowTK().id);
    }
}
