package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class BlockItem {
    static void BlockItemAnalysis() {
        Token token = Tools.GetNextTK();
        if(token.tk.equals("INTTK") || token.tk.equals("CHARTK"))
        {
            Decl.DeclAnalysis();
        }
        else{
            LVal.LValAnalysis();
        }
    }
}
