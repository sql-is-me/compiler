package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class Exp {
    static ArrayList<Token> ExpAnalysis() {
        ArrayList<Token> expTokenList = new ArrayList<>(); // 记录exp开始
        int begin = CompUnit.count + 1; // 记录Exp开始

        AddExp.AddExpAnalysis();

        int end = CompUnit.count; // 记录Exp结束

        expTokenList = Tools.GetExpfromIndex(begin, end);

        Tools.WriteLine(Syntax.NodeType.Exp, Tools.GetNowTK().id);

        return expTokenList;
    }
}
