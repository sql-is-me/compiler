package Frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.Collections;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class ConstInitVal {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    static ArrayList<Integer> ConstInitValAnalysis(int size) {
        ArrayList<Integer> values = new ArrayList<>();

        if (Tools.LookNextTK().tk.equals("STRCON")) {
            Token t = Tools.LookNextTK();
            for (char c : t.str.toCharArray()) {
                values.add((int) c);
            }

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
        
        if (values.size() < size) {
            values.addAll(Collections.nCopies(size - values.size(), 0));
        }
        return values;
    }
}