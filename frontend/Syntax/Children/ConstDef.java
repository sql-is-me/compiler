package frontend.Syntax.Children;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;

public class ConstDef {
    static Pair ConstDefAnalysis() {
        Pair pair = new Pair(null, null);
        int begin = 0, end = 0;

        if (Tools.LookNextTK().tk.equals("IDENFR")) {
            CompUnit.count++;
            pair.name = Tools.GetNowTK().str;

            if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                ConstInitVal.ConstInitValAnalysis();
            } else if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++; // [
                begin = CompUnit.count; // record begin
                ConstExp.ConstExpAnalysis();
                end = CompUnit.count; // record end

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                } else {
                    Token temp = Tools.GetNowTK();
                    ErrorLog.makelog_error(temp.line, 'k');
                }
                
                if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                    CompUnit.count++;
                    ConstInitVal.ConstInitValAnalysis();
                }
            }
        }

        Tools.WriteLine(Syntax.NodeType.ConstDef, Tools.GetNowTK().id);

        return pair;
    }
}
