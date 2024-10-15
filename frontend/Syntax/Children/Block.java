package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax.NodeType;

public class Block {
    static void BlockAnalysis() {
        if (Tools.getToken(CompUnit.count).tk.equals("LBRACE")) {
            BlockItem.BlockItemAnalysis();
        } else {
            // wrong
        }

        Tools.WriteLine(NodeType.Block);
    }
}
