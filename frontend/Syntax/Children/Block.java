package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.Syntax.Syntax;

public class Block {
    static void BlockAnalysis(boolean isFunc) {
        CompUnit.count++; // {

        if (!isFunc) { // 如果不是函数调用的话就新建符号表
            utils.createSymTab(utils.curSymTab);
        }

        while (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
            BlockItem.BlockItemAnalysis();
        }
        CompUnit.count++; // }
        utils.jumpOutSymTab();

        Tools.WriteLine(Syntax.NodeType.Block, Tools.GetNowTK().id);
    }
}
