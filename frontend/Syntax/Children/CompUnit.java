package Frontend.Syntax.Children;

import java.util.List;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Syntax;

public class CompUnit {
    public static int count = -1;
    public static List<Token> words = Lexer.tokens;

    public static void CompUnitAnalysis() {
        Token token;
        while (true) {
            token = Tools.LookNextTK();
            if ((token.tk.equals("INTTK") || token.tk.equals("CHARTK")
                    || token.tk.equals("VOIDTK"))
                    && Tools.GetCountTK(CompUnit.count + 2).tk.equals("IDENFR")
                    && Tools.GetCountTK(CompUnit.count + 3).tk.equals("LPARENT")) { // Type ident (

                if (Tools.inGlobal) { // 在第一次触发函数定义时，将全局变量标志置为false
                    Tools.inGlobal = false;
                }

                FuncDef.FuncDefAnalysis();
            } else if ((token.tk.equals("INTTK") || token.tk.equals("CHARTK"))
                    && Tools.GetCountTK(CompUnit.count + 2).tk.equals("IDENFR")) { // Type ident

                Decl.DeclAnalysis();
            } else if (token.tk.equals("CONSTTK")) { // const
                Decl.DeclAnalysis();
            } else { //
                break;
            }
        }

        if (Tools.LookNextTK().tk.equals("INTTK") && Tools.GetCountTK(count + 2).tk.equals("MAINTK")
                && Tools.GetCountTK(count + 3).tk.equals("LPARENT")) {
            CompUnit.count += 3; // (
            MainFuncDef.MainFuncDefAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.CompUnit, Tools.GetNowTK().id);
    }
}
