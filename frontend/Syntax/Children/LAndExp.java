package frontend.Syntax.Children;

public class LAndExp {
    static void LAndExpAnalysis(int expsize) {
        int count = CompUnit.count + 1;
        int size = 0;
        while (Tools.GetCountTK(count).tk.equals("AND") || Tools.GetCountTK(count).tk.equals("EQL")
                || Tools.GetCountTK(count).tk.equals("NEQ") || Tools.GetCountTK(count).tk.equals("LSS")
                || Tools.GetCountTK(count).tk.equals("LEQ") || Tools.GetCountTK(count).tk.equals("GRE")
                || Tools.GetCountTK(count).tk.equals("GEQ")) {// && == != < <= > >=

            if (!Tools.GetCountTK(count).tk.equals("AND")) {
                size += 2;
            } else if (Tools.GetCountTK(count).tk.equals("AND")) {
                // MulExp.MulExpAnalysis(size);
                CompUnit.count++;
                size = 1;
            }
            count += 2;
        }
        EqExp.EqExpAnalysis(size);
    }

}
