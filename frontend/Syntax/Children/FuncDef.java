package frontend.Syntax.Children;

import java.util.ArrayList;

import SymbolTable.utils;
import SymbolTable.Symbol.TokenType;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncDef {
    static void FuncDefAnalysis() {
        TokenType returntype;
        FuncPart fp = new FuncPart(null, 0, null);

        returntype = FuncType.FuncTypeAnalysis();
        fp.name = Tools.LookNextTK().str; // get inden name
        CompUnit.count += 2; // IDENFR (

        utils.createSymTab(utils.curSymTab.id); // jump in

        if (!Tools.LookNextTK().tk.equals("RPARENT") && !Tools.LookNextTK().tk.equals("LBRACE")) { // ) {
            fp.paramTypes = FuncFParams.FuncFParamsAnalysis();
            fp.paramCount = fp.paramTypes.size();
        }
        if (!Tools.LookNextTK().tk.equals("RPARENT")) { // 缺)
            Token temp = Tools.GetNowTK();
            ErrorLog.makelog_error(temp.line, 'j');
        } else {
            CompUnit.count++; // )
        }
        Block.BlockAnalysis();

        Tools.WriteLine(Syntax.NodeType.FuncDef, Tools.GetNowTK().id);

        Tools.AddFuncSymbol(returntype, fp);
    }
}
