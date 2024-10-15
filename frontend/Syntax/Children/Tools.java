package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Syntax.Syntax.NodeType;

public class Tools {
    public static Token GetCountTK(int count) {
        return CompUnit.words.get(count);
    }

    public static Token GetNowTK() {
        return CompUnit.words.get(CompUnit.count);
    }

    public static Token GetNextTK() { // 找到下一个TK
        CompUnit.count++;
        return CompUnit.words.get(CompUnit.count);
    }

    public static Token LookNextTK() { // 看下一个TK
        return CompUnit.words.get(CompUnit.count + 1);
    }

    public static void WriteLine(Token token) { // 写
        Syntax.getParser().add(token.toString());
    }

    // public static void WriteLine(Node node) {
    // Syntax.getParser().add(node.toString());
    // }

    public static void WriteLine(NodeType node) {
        Syntax.getParser().add("<" + node.toString() + ">\n");
    }
}
