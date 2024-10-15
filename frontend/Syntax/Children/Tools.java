package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Syntax.Syntax.NodeType;

import java.util.List;
import java.util.ArrayList;

public class Tools {
    public static Token getToken(int count) {
        return CompUnit.words.get(count);
    }

    public static Token FindNextTk(int count) { // 找到下一个TK
        count++;
        return CompUnit.words.get(count);
    }

    public static Token LookNextTk(int count) { // 看下一个TK
        return CompUnit.words.get(count + 1);
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
