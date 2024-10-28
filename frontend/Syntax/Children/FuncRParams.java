package frontend.Syntax.Children;

import java.util.ArrayList;

import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;
import frontend.Pair;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncRParams {
    static Pair FuncRParamsAnalysis() {
        int funcRParamsNumber = 1;
        ArrayList<VarTypes> funcParamsTypes = new ArrayList<>();
        ArrayList<Token> expTokenList;
        Pair pair = new Pair(null, null);

        expTokenList = Exp.ExpAnalysis(true);
        funcParamsTypes.add(VarTypes.valueOf(utils.JudgeExpType(expTokenList)));

        while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
            CompUnit.count++;
            expTokenList = Exp.ExpAnalysis(true);
            funcParamsTypes.add(VarTypes.valueOf(utils.JudgeExpType(expTokenList)));
            funcRParamsNumber++;
        }

        Tools.WriteLine(Syntax.NodeType.FuncRParams, Tools.GetNowTK().id);
        pair.a = funcRParamsNumber;
        pair.b = funcParamsTypes;

        return pair;
    }

}
