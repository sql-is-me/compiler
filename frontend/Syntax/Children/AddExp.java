package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class AddExp {
    static void AddExpAnalysis() {
        int count = CompUnit.count + 2; // 直接跳一位看符号
        int size = 1; // Mul表达式的长度
        while (Tools.GetCountTK(count).tk.equals("PLUS") || Tools.GetCountTK(count).tk.equals("MINU")
                || Tools.GetCountTK(count).tk.equals("MULT") || Tools.GetCountTK(count).tk.equals("DIV")
                || Tools.GetCountTK(count).tk.equals("MOD")) { //
            if (!Tools.GetCountTK(count).tk.equals("PLUS")
                    && !Tools.GetCountTK(count).tk.equals("MINU")) {
                size += 2;
            } else if (Tools.GetCountTK(count).tk.equals("PLUS")
                    || Tools.GetCountTK(count).tk.equals("MINU")) {
                MulExp.MulExpAnalysis(size);
                CompUnit.count++; // 踩在+ - ;上
                size = 1;
            }
            count += 2;
        }
        MulExp.MulExpAnalysis(size);
    }

    static void AddExpAnalysis(int expsize) {
        int size = 1;

        for (int count = CompUnit.count + 2; count - CompUnit.count < expsize; count += 2) {
            Token token = Tools.GetCountTK(count);
            if (!token.tk.equals("PLUS") && !token.tk.equals("MINU")) {
                size += 2;
            } else if (token.tk.equals("PLUS") || token.tk.equals("MINU")) {
                MulExp.MulExpAnalysis(size);
                CompUnit.count++; // + -
                size = 1;
            }
        }
        MulExp.MulExpAnalysis(size);
    }
}
