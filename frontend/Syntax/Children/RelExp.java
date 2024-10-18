package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class RelExp {
    static void RelExpAnalysis(int expsize) {
        int size = 1;

        for (int count = CompUnit.count + 2; count - CompUnit.count < expsize; count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("LSS") && !token.tk.equals("LEQ") && !token.tk.equals("GRE")
                    && !token.tk.equals("GEQ")) {
                size += 2;

            } else if (token.tk.equals("LSS") || token.tk.equals("LEQ") || token.tk.equals("GRE")
                    || token.tk.equals("GEQ")) {
                AddExp.AddExpAnalysis(size);

                Tools.WriteLine(Syntax.NodeType.RelExp, Tools.GetNowTK().id);
                CompUnit.count++; // < <= > >=
                size = 1;
            }
        }
        AddExp.AddExpAnalysis(size);
        Tools.WriteLine(Syntax.NodeType.RelExp, Tools.GetNowTK().id);
    }
}
