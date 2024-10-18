package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class LAndExp {
    static void LAndExpAnalysis(int expsize) {
        int size = 1;

        for (int count = CompUnit.count + 2; count - CompUnit.count < expsize; count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("AND")) {
                size += 2;

            } else if (token.tk.equals("AND")) {
                EqExp.EqExpAnalysis(size);

                Tools.WriteLine(Syntax.NodeType.LAndExp, Tools.GetNowTK().id);
                CompUnit.count++; // &&
                size = 1;
            }
        }
        EqExp.EqExpAnalysis(size);
        Tools.WriteLine(Syntax.NodeType.LAndExp, Tools.GetNowTK().id);
    }
}
