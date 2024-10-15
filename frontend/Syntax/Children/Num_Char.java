package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class Num_Char {
    static void NumberAnalysis() {
        Token token = UnaryExp.tokenArr.get(UnaryExp.count);
        if (token.tk.equals("INTCON")) {
            UnaryExp.count++;
            Tools.WriteLine(Syntax.NodeType.Number);
        }
    }

    static void CharacterAnalysis() {
        Token token = UnaryExp.tokenArr.get(UnaryExp.count);
        if (token.tk.equals("CHARCON")) {
            UnaryExp.count++;
            Tools.WriteLine(Syntax.NodeType.Character);
        }
    }
}
