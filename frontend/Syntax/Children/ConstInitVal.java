package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstInitVal {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    static void ConstInitValAnalysis() {
        Token token = Tools.FindNextTk(CompUnit.count);
        if (token.tk.equals("STRCON")) {
        } else if (token.tk.equals("LBRACE")) {
            token = Tools.FindNextTk(CompUnit.count);
            while (!Tools.getToken(CompUnit.count + 1).tk.equals("COMMA")) {
                ConstExp.ConstExpAnalysis();
                CompUnit.count += 2;
            }
            ConstExp.ConstExpAnalysis();
            CompUnit.count++;
        } else {
            ConstExp.ConstExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.ConstInitVal);
    }
}