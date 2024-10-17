package frontend.Syntax.Children;

import frontend.Syntax.Syntax.NodeType;

public class Block {
    static void BlockAnalysis() {
        CompUnit.count++; // {
        BlockItem.BlockItemAnalysis();
        CompUnit.count++; // }
    }
}
