package frontend.Syntax.Children;

public class FuncType {
    static void FuncTypeAnalysis() {
        CompUnit.count++;
        if ((Tools.GetNowTK().tk.equals("INTTK") || Tools.GetNowTK().tk.equals("CHARTK"))
                || Tools.GetNowTK().tk.equals("VOIDTK")) {
            // write
        }
    }
}
