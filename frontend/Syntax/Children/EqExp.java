package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class EqExp {
    static void EqExpAnalysis(int expsize) {
        int size = 1;

        for (int count = CompUnit.count + 2; count - CompUnit.count < expsize; count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("EQL") && !token.tk.equals("NEQ")) {
                size += 2;

            } else if (token.tk.equals("EQL") || token.tk.equals("NEQ")) {
                RelExp.RelExpAnalysis(size);

                Tools.WriteLine(Syntax.NodeType.EqExp, Tools.GetNowTK().id);
                CompUnit.count++; // == !=
                size = 1;
            }
        }
        RelExp.RelExpAnalysis(size);
        Tools.WriteLine(Syntax.NodeType.EqExp, Tools.GetNowTK().id);
    }
}
