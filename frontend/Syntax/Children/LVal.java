package Frontend.Syntax.Children;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.VarSymbol;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class LVal {
    static VarTypes LValAnalysis() {
        CompUnit.count++; // IDENFR
        VarTypes varType;

        if (utils.JudgeUndefined(Tools.GetNowTK())) {
            varType = VarTypes.Undefined;
        } else {
            VarSymbol varSymbol = (VarSymbol) utils.GetIdenfr(Tools.GetNowTK().str);
            varType = varSymbol.type;
        }

        Token token = Tools.LookNextTK();
        if (token.tk.equals("LBRACK")) { // [
            CompUnit.count++; // [

            Exp.ExpAnalysis(); // FIXME
            if (!Tools.LookNextTK().tk.equals("RBRACK")) { // 缺 ]
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'k');
            } else {
                CompUnit.count++; // ]
            }
        }

        Tools.WriteLine(Syntax.NodeType.LVal, Tools.GetNowTK().id);

        return varType;
    }
}
