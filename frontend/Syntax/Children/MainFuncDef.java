package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class MainFuncDef {
    static void MainFuncDefAnalysis() {
        if (Tools.getToken(CompUnit.count).tk.equals("INTTK")) { // int
            CompUnit.count++;
            if (Tools.getToken(CompUnit.count).tk.equals("MAINTK")) { // main
                CompUnit.count++;
                if (Tools.getToken(CompUnit.count).tk.equals("LPARENT")) { // (
                    CompUnit.count++;
                    if (Tools.getToken(CompUnit.count).tk.equals("RPARENT")) { // )
                        CompUnit.count++;
                        if (Tools.getToken(CompUnit.count).tk.equals("LBRACE")) { // {
                            Block.BlockAnalysis();
                        } else {
                            // wrong
                        }
                    } else {
                        // wrong
                    }
                } else {
                    // wrong
                }
            } else {
                // wrong
            }
        } else {
            // wrong
        }
    }
}
