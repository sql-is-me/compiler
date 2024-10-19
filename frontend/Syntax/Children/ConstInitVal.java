package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class ConstInitVal {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    static void ConstInitValAnalysis() {
        if (Tools.LookNextTK().tk.equals("STRCON")) {
            CompUnit.count++;
        } else if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            CompUnit.count++; // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                ConstExp.ConstExpAnalysis();
                while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
                    CompUnit.count++; // ,
                    ConstExp.ConstExpAnalysis();
                }
            }
            CompUnit.count++; // }
        } else {
            ConstExp.ConstExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.ConstInitVal, Tools.GetNowTK().id);
    }
}