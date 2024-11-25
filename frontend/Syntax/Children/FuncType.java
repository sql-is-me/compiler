package Frontend.Syntax.Children;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import SymbolTable.FuncSymbol.FuncTypes;

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
