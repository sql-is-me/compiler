package frontend.Syntax.Children;

import SymbolTable.utils;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncDef {
    static void FuncDefAnalysis() {
        FuncPart fp = new FuncPart(null, null, 0, null);

        fp.returnType = FuncType.FuncTypeAnalysis();
        fp.name = Tools.LookNextTK().str; // get indent name
        CompUnit.count++; // IDENFR
        utils.JudgeRepeat(Tools.GetNowTK()); // 判断是否重定义

        CompUnit.count++; // (

        utils.createSymTab(utils.curSymTab); // jump in

        if (!Tools.LookNextTK().tk.equals("RPARENT") && !Tools.LookNextTK().tk.equals("LBRACE")) { // ) {
            fp.paramTypes = FuncFParams.FuncFParamsAnalysis();
            fp.paramNumber = fp.paramTypes.size();
        }

        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }

        utils.SetfuncType(fp.returnType); // in function block
        Block.BlockAnalysis();
        if (utils.isinFunc()) { // 退出函数时，判断有无return
            utils.JudgeReturnExist(Tools.GetNowTK()); // }
            utils.SetfuncTypetoNull();
        }

        Tools.WriteLine(Syntax.NodeType.FuncDef, Tools.GetNowTK().id);

        Tools.AddFuncSymbol(fp);
    }
}
