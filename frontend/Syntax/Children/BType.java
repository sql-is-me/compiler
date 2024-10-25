package frontend.Syntax.Children;

public class BType {
    static String BTypeAnalysis() {
        CompUnit.count++;
        if (Tools.GetNowTK().tk.equals("INTTK")) {
            return "Int";
        } else {
            return "Char";
        }
    }
}