package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class InitVal {
    static ArrayList<ArrayList<Token>> InitValAnalysis() {
        ArrayList<ArrayList<Token>> initValues = new ArrayList<>();
        ArrayList<Token> temp = new ArrayList<>();

        if (Tools.LookNextTK().tk.equals("STRCON")) {
            temp.add(Tools.LookNextTK());
            initValues.add(temp);

            CompUnit.count++;
        } else if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            CompUnit.count++; // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                temp = Exp.ExpAnalysis();
                initValues.add(temp);
                while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
                    CompUnit.count++;
                    temp = Exp.ExpAnalysis();
                    initValues.add(temp);
                }
                CompUnit.count++; // }
            } else {
                CompUnit.count++;
            }
        } else {
            temp = Exp.ExpAnalysis();
            initValues.add(temp);
        }

        Tools.WriteLine(Syntax.NodeType.InitVal, Tools.GetNowTK().id);
        return initValues;
    }
}
