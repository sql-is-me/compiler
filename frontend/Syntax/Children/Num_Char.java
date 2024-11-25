package Frontend.Syntax.Children;

import Frontend.Syntax.Syntax;

public class Num_Char {
    static void NumberAnalysis() {
        CompUnit.count++;

        Tools.WriteLine(Syntax.NodeType.Number, Tools.GetNowTK().id);
    }

    static void CharacterAnalysis() {
        CompUnit.count++;

        Tools.WriteLine(Syntax.NodeType.Character, Tools.GetNowTK().id);
    }
}
