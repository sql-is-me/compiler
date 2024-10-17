package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

public class Stmt {
    static void StmtAnalysis() {
        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR")) {
            LVal.LValAnalysis();
            if (Tools.LookNextTK().tk.equals("ASSIGN")) {
                CompUnit.count++; // =
                if (!Tools.LookNextTK().tk.equals("GETINTTK") && !Tools.LookNextTK().tk.equals("GETCHARTK")) {
                    Exp.ExpAnalysis();
                    CompUnit.count++; // ;
                } else {
                    CompUnit.count += 3; // get ( )
                }
                if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                    // error
                }
            }
        } else if (token.tk.equals("LBRACE")) {
            Block.BlockAnalysis();
        } else if (token.tk.equals("IFTK")) {
            CompUnit.count++; // if

            if (Tools.LookNextTK().tk.equals("LPARENT")) {
                CompUnit.count++; // (
                Cond.CondAnalysis();
                CompUnit.count++; // )
                Stmt.StmtAnalysis();
                if (Tools.LookNextTK().tk.equals("ELSETK")) {
                    CompUnit.count++; // else
                    Stmt.StmtAnalysis();
                }
            }
        } else if (token.tk.equals("FORTK")) {
            CompUnit.count++; // for
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
            CompUnit.count++; // break or continue
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                //
            }
        } else if (token.tk.equals("RETURNTK")) {
            CompUnit.count++;
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            }
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                // error
            } else {
                CompUnit.count++; // ;
            }
        } else if (token.tk.equals("PRINTFTK")) {
            CompUnit.count++;
            if (Tools.LookNextTK().tk.equals("LPARENT")) {
                CompUnit.count++;
                if (Tools.LookNextTK().tk.equals("STRCON")) {
                    CompUnit.count++;
                    while (Tools.LookNextTK().tk.equals("COMMA")) {
                        CompUnit.count++; // ,
                        Exp.ExpAnalysis();
                    }
                    CompUnit.count++; // )
                    if (!Tools.GetNextTK().tk.equals("SEMICN")) {
                        // error
                    }
                }
            }
        } else {
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            }
            if (!Tools.LookNextTK().tk.equals("SEMICN")) {
                // error
            } else {
                CompUnit.count++; // ;
            }
        }
    }
}
