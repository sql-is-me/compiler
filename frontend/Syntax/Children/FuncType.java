package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class FuncType {
    static void FuncTypeAnalysis() {
        if ((Tools.LookNextTK().tk.equals("INTTK") || Tools.LookNextTK().tk.equals("CHARTK"))
                || Tools.LookNextTK().tk.equals("VOIDTK")) {
                    CompUnit.count++;
        }

        Tools.WriteLine(Syntax.NodeType.FuncType, Tools.GetNowTK().id);
    }
}
