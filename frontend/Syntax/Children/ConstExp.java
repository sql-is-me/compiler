package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;
import Frontend.Syntax.utils;

public class ConstExp {
    static int ConstExpAnalysis() {
        int start = CompUnit.count + 1;
        AddExp.AddExpAnalysis();
        int end = CompUnit.count;

        ArrayList<Token> Exp = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            Exp.add(Tools.GetCountTK(i));
        }

        utils.addExp(Exp);
        int value = Tools.calConstExp(Exp);

        Tools.WriteLine(Syntax.NodeType.ConstExp, Tools.GetNowTK().id);
        return value;
    }
}
