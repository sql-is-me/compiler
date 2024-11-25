package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class Exp {
    static void ExpAnalysis() {

        AddExp.AddExpAnalysis();

        Tools.WriteLine(Syntax.NodeType.Exp, Tools.GetNowTK().id);
    }

    static ArrayList<Token> ExpAnalysis(boolean returnArrayList) {
        ArrayList<Token> expTokenList = new ArrayList<>(); // 记录exp开始
        int count = CompUnit.count + 1; // 记录Exp开始

        AddExp.AddExpAnalysis();

        int end = CompUnit.count; // 记录Exp结束

        for (; count <= end; count++) {
            expTokenList.add(Tools.GetCountTK(count));
        }

        Tools.WriteLine(Syntax.NodeType.Exp, Tools.GetNowTK().id);

        return expTokenList;
    }
}
