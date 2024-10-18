package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class Block {
    static void BlockAnalysis() {
        CompUnit.count++; // {
        while (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
            BlockItem.BlockItemAnalysis();
        }
        CompUnit.count++; // }

        Tools.WriteLine(Syntax.NodeType.Block, Tools.GetNowTK().id);
    }
}
