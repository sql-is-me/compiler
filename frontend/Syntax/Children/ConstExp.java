package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class ConstExp {
    static int ConstExpAnalysis() {
        int start = CompUnit.count;
        AddExp.AddExpAnalysis();
        int end = CompUnit.count;

        ArrayList<Token> Exp = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            Exp.add(Tools.GetCountTK(i));
        }

        int value = Tools.calConstExp(Exp);

        Tools.WriteLine(Syntax.NodeType.ConstExp, Tools.GetNowTK().id);
        return value;
    }
}
