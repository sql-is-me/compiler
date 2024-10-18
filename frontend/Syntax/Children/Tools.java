package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Node;
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

    public static void WriteLine(NodeType type, int index) {
        Node node = new Node(type, index);
        Syntax.getNodes().add(node);
    }
}
