package frontend.Syntax.Children;

import frontend.Syntax.Syntax.NodeType;

public class Block {
    static void BlockAnalysis() {
        if (Tools.GetNowTK().tk.equals("LBRACE")) { // {
            BlockItem.BlockItemAnalysis();
            CompUnit.count++; // }
        } else {
            // wrong
        }

        Tools.WriteLine(NodeType.Block);
    }
}
