package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.Syntax.Syntax;

public class Block {
    static void BlockAnalysis() {
        CompUnit.count++; // {
        while (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
            BlockItem.BlockItemAnalysis();
        }
        CompUnit.count++; // }
        
        utils.jumpOutofBlock(); // jump out

        Tools.WriteLine(Syntax.NodeType.Block, Tools.GetNowTK().id);
    }
}
