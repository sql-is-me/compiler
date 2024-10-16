package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class Stmt {
    static void StmtAnalysis() {
        Token token = Tools.GetNowTK();
        if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
            token = Tools.GetNextTK();
            if (token.tk.equals("ASSIGN")) {
                if (!Tools.LookNextTK().tk.equals("GETINTTK") && !Tools.LookNextTK().tk.equals("GETCHARTK")) {
                    Exp.ExpAnalysis();
                    // ;
                }
            }
        } else if (token.tk.equals("LBRACK")) {
            Exp.ExpAnalysis();
        } else if (token.tk.equals("LBRACE")) {
            LVal.LValAnalysis();
        } else if (token.tk.equals("IFTK")) {
            if (Tools.GetNextTK().tk.equals("LPARENT")) {
                Cond.CondAnalysis();
                CompUnit.count++; // )
                Stmt.StmtAnalysis();
                if (Tools.LookNextTK().tk.equals("ELSETK")) {
                    CompUnit.count++; // else
                    Stmt.StmtAnalysis();
                }
            }
        } else if (token.tk.equals("FORTK")) {
            CompUnit.count++; // (
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                ForStmt.ForStmtAnalysis();
            }
            CompUnit.count++; // ;
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                Cond.CondAnalysis();
            }
            CompUnit.count++; // ;
            if (!Tools.LookNextTK().tk.equals("RPARENT")) {
                ForStmt.ForStmtAnalysis();
            }
            CompUnit.count++; // )
            Stmt.StmtAnalysis();

        } else if (token.tk.equals("BREAKTK") || token.tk.equals("CONTINUETK")) {
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                //
            }
        } else if (token.tk.equals("RETURNTK")) {
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTTK")
                    || Tools.LookNextTK().tk.equals("CHARTK") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("IDENFR") || Tools.LookNextTK().tk.equals("PLUS")
                    || Tools.LookNextTK().tk.equals("MINU") || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            } else {
                if (!Tools.GetNextTK().tk.equals("SEMICN")) {
                    //
                } else {
                    CompUnit.count++; // ;
                }
            }
        } else if (token.tk.equals("PRINTFTK")) {
            if (Tools.GetNextTK().tk.equals("LPARENT")) {
                if (Tools.GetNextTK().tk.equals("STRCON")) {
                    Exp.ExpAnalysis();
                    while (Tools.LookNextTK().tk.equals("COMMA")) {
                        CompUnit.count++;
                        Exp.ExpAnalysis();
                    }
                    if (!Tools.GetNextTK().tk.equals("SEMICN")) {
                        //
                    }
                }

            }
        }

    }
}
