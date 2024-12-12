package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Pair;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class FuncRParams {
    static Pair FuncRParamsAnalysis() {
        int funcRParamsNumber = 1;
        ArrayList<VarTypes> funcParamsTypes = new ArrayList<>();
        ArrayList<Token> expTokenList;
        Pair pair = new Pair(null, null);

        expTokenList = Exp.ExpAnalysis();
        funcParamsTypes.add(VarTypes.valueOf(utils.JudgeExpType(expTokenList)));

        while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
            CompUnit.count++;
            expTokenList = Exp.ExpAnalysis();
            funcParamsTypes.add(VarTypes.valueOf(utils.JudgeExpType(expTokenList)));
            funcRParamsNumber++;
        }

        Tools.WriteLine(Syntax.NodeType.FuncRParams, Tools.GetNowTK().id);
        pair.a = funcRParamsNumber;
        pair.b = funcParamsTypes;

        return pair;
    }

}
