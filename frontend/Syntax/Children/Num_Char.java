package frontend.Syntax.Children;

import frontend.Syntax.Syntax;

public class Num_Char {
    static void NumberAnalysis() {
        CompUnit.count++;
        Tools.WriteLine(Syntax.NodeType.Number);
    }

    static void CharacterAnalysis() {
        CompUnit.count++;
        Tools.WriteLine(Syntax.NodeType.Character);
    }
}
