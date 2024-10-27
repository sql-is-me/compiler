package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }

        utils.createSymTab(utils.curSymTab); // jump in

        if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            Block.BlockAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.MainFuncDef, Tools.GetNowTK().id);
    }
}
