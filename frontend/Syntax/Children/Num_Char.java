package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class Num_Char {
    static void NumberAnalysis() {
        Tools.WriteLine(Syntax.NodeType.Number);
    }

    static void CharacterAnalysis() {
        Tools.WriteLine(Syntax.NodeType.Character);
    }
}
