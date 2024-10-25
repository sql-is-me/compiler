package frontend.Syntax.Children;

import frontend.Syntax.Syntax;
import SymbolTable.Symbol.TokenType;

import java.util.ArrayList;

public class FuncFParams {
    static ArrayList<TokenType> FuncFParamsAnalysis() {
        ArrayList<TokenType> funcParams = new ArrayList<>();
        TokenType tt;

        tt = FuncFParam.FuncFParamAnalysis();
        funcParams.add(tt);
        while (Tools.LookNextTK().tk.equals("COMMA")) {
            CompUnit.count++; // ,
            tt = FuncFParam.FuncFParamAnalysis();
            funcParams.add(tt);
        }

        Tools.WriteLine(Syntax.NodeType.FuncFParams, Tools.GetNowTK().id);

        return funcParams;
    }
}
