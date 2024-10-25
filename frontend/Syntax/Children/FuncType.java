package frontend.Syntax.Children;

import SymbolTable.Symbol.TokenType;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class FuncType {
    static TokenType FuncTypeAnalysis() {
        TokenType tt;
        Token token = Tools.LookNextTK();

        if (token.tk.equals("INTTK")) {
            tt = TokenType.IntFunc;
        } else if (token.tk.equals("CHARTK")) {
            tt = TokenType.CharFunc;
        } else {
            tt = TokenType.VoidFunc;
        }
        CompUnit.count++; // return type

        Tools.WriteLine(Syntax.NodeType.FuncType, Tools.GetNowTK().id);

        return tt;
    }
}
