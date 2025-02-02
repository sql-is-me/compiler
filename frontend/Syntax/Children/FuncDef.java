package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;

public class FuncDef {
    static void FuncDefAnalysis() {
        FuncPart fp = new FuncPart(null, null, 0, null);

        fp.returnType = FuncType.FuncTypeAnalysis();
        fp.name = Tools.LookNextTK().str; // get indent name
        CompUnit.count++; // IDENFR

        boolean isFuncRepeat = utils.JudgeRepeat(Tools.GetNowTK()); // 判断是否重定义

        CompUnit.count++; // (

        utils.createSymTab(utils.curSymTab); // jump in

        if (!Tools.LookNextTK().tk.equals("RPARENT") && !Tools.LookNextTK().tk.equals("LBRACE")) { // ) {
            fp.paramTypes = FuncFParams.FuncFParamsAnalysis();
            fp.paramNumber = fp.paramTypes.size();
        } else { // 无参数情况
            fp.paramTypes = new ArrayList<>();
            fp.paramNumber = 0;
        }

        fp.offset = CompUnit.count + 1; // )
        Tools.AddFuncSymbol(fp, isFuncRepeat);

        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }

        utils.SetfuncType(fp.returnType); // in function block

        Block.BlockAnalysis(true);

        utils.JudgeReturnExist(Tools.GetNowTK()); // } // 退出函数时，判断最后有无return

        Tools.WriteLine(Syntax.NodeType.FuncDef, Tools.GetNowTK().id);
    }
}
