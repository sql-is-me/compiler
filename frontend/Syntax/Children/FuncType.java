package frontend.Syntax.Children;

import SymbolTable.FuncSymbol.FuncTypes;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncType {
    static FuncTypes FuncTypeAnalysis() {
        FuncTypes tt;
        Token token = Tools.LookNextTK();

        if (token.tk.equals("INTTK")) {
            tt = FuncTypes.IntFunc;
        } else if (token.tk.equals("CHARTK")) {
            tt = FuncTypes.CharFunc;
        } else {
            tt = FuncTypes.VoidFunc;
        }
        CompUnit.count++; // return type

        Tools.WriteLine(Syntax.NodeType.FuncType, Tools.GetNowTK().id);

        return tt;
    }
}
