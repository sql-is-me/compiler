package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.FuncSymbol.FuncTypes;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }

        utils.createSymTab(utils.curSymTab); // jump in

        utils.SetfuncType(FuncTypes.IntFunc);

        Block.BlockAnalysis(true);

        // 退出函数时，判断有无return
        utils.JudgeReturnExist(Tools.GetNowTK()); // }

        Tools.WriteLine(Syntax.NodeType.MainFuncDef, Tools.GetNowTK().id);
    }
}
