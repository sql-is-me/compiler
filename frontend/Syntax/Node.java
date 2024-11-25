package Frontend.Syntax;

public class Node {
    String name;
    public Integer line;

    public Node(Syntax.NodeType type, Integer index) {
        this.name = type.toString();
        this.line = index;
    }

    @Override
    public String toString() {
        return "<" + name + ">\n";
    }
}
