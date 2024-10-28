package frontend.Syntax.Children;

import SymbolTable.utils;
import SymbolTable.FuncSymbol.FuncTypes;
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

        utils.SetfuncType(FuncTypes.IntFunc);

        utils.createSymTab(utils.curSymTab); // jump in
        Block.BlockAnalysis();

        // 退出函数时，判断有无return
        utils.JudgeReturnExist(Tools.GetNowTK()); // }

        Tools.WriteLine(Syntax.NodeType.MainFuncDef, Tools.GetNowTK().id);
    }
}
