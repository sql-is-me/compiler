package frontend.Syntax.Children;

import frontend.ErrorLog;

public class ConstDef {
    static void ConstDefAnalysis() {
        if (Tools.LookNextTK().tk.equals("IDENFR")) {
            CompUnit.count++;

            if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
                CompUnit.count++;
                ConstInitVal.ConstInitValAnalysis();
            } else if (Tools.LookNextTK().tk.equals("LBRACK")) { // [
                CompUnit.count++;
                ConstExp.ConstExpAnalysis();

                if (Tools.LookNextTK().tk.equals("RBRACK")) { // ]
                    CompUnit.count++;
                    
                    if (Tools.GetNextTK().tk.equals("ASSIGN")) { // =
                        CompUnit.count++;
                        ConstInitVal.ConstInitValAnalysis();
                    }
                } else {
                    // error
                }
            }
        }

        // token = Tools.GetNextTK(CompUnit.count);
        // if (!token.tk.equals("SEMICN")) {
        // ErrorLog.makelog_error(token.line, 'i');
        // CompUnit.count--;
        // }

    }
}
