package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Syntax.Syntax;

public class ConstInitVal {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    static ArrayList<Integer> ConstInitValAnalysis() {
        ArrayList<Integer> values = new ArrayList<>();

        if (Tools.LookNextTK().tk.equals("STRCON")) {
            CompUnit.count++;
        } else if (Tools.LookNextTK().tk.equals("LBRACE")) { // {
            CompUnit.count++; // {
            if (!Tools.LookNextTK().tk.equals("RBRACE")) { // }
                values.add(ConstExp.ConstExpAnalysis());
                while (Tools.LookNextTK().tk.equals("COMMA")) { // ,
                    CompUnit.count++; // ,
                    values.add(ConstExp.ConstExpAnalysis());
                }
            }
            CompUnit.count++; // }
        } else {
            values.add(ConstExp.ConstExpAnalysis());
        }

        Tools.WriteLine(Syntax.NodeType.ConstInitVal, Tools.GetNowTK().id);
        return values;
    }
}