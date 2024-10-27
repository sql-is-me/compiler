package frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.List;

import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Syntax.Node;
import SymbolTable.utils;

public class Stmt {
    private static Boolean tryLVal() {
        int count = CompUnit.count;
        List<Node> temp = new ArrayList<>(Syntax.getNodes());

        LVal.LValAnalysis();
        if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
            return true;
        } else {
            CompUnit.count = count;
            Syntax.getNodes().clear();
            Syntax.getNodes().addAll(temp);
            return false;
        }
    }

    static void StmtAnalysis() {
        Token token = Tools.LookNextTK();
        if (token.tk.equals("IDENFR") && (Tools.GetCountTK(CompUnit.count + 2).tk.equals("ASSIGN")
                || Tools.GetCountTK(CompUnit.count + 2).tk.equals("LBRACK"))) {
            if (tryLVal()) {
                CompUnit.count++; // =
                if (!Tools.LookNextTK().tk.equals("GETINTTK") && !Tools.LookNextTK().tk.equals("GETCHARTK")) {
                    Exp.ExpAnalysis();
                    if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                        Token tempToken = Tools.GetNowTK();
                        ErrorLog.makelog_error(tempToken.line, 'i');
                    } else {
                        CompUnit.count++; // ;
                    }
                } else {
                    CompUnit.count += 2; // get (
                    if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                        Token tempToken = Tools.GetNowTK();
                        ErrorLog.makelog_error(tempToken.line, 'j');
                    } else {
                        CompUnit.count++; // )
                    }

                    if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                        Token tempToken = Tools.GetNowTK();
                        ErrorLog.makelog_error(tempToken.line, 'i');
                    } else {
                        CompUnit.count++; // ;
                    }
                }
            } else {
                Exp.ExpAnalysis();

                if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                    Token tempToken = Tools.GetNowTK();
                    ErrorLog.makelog_error(tempToken.line, 'i');
                } else {
                    CompUnit.count++; // ;
                }
            }

        } else if (token.tk.equals("LBRACE")) {
            utils.createSymTab(utils.curSymTab); // jump in
            Block.BlockAnalysis();
        } else if (token.tk.equals("IFTK")) {
            CompUnit.count++; // if

            if (Tools.LookNextTK().tk.equals("LPARENT")) {
                CompUnit.count++; // (
                Cond.CondAnalysis();

                if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                    Token tempToken = Tools.GetNowTK();
                    ErrorLog.makelog_error(tempToken.line, 'j');
                } else {
                    CompUnit.count++; // )
                }

                Stmt.StmtAnalysis();
                if (Tools.LookNextTK().tk.equals("ELSETK")) {
                    CompUnit.count++; // else
                    Stmt.StmtAnalysis();
                }
            }
        } else if (token.tk.equals("FORTK")) {
            CompUnit.count += 2; // for (
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
            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'i');
            } else {
                CompUnit.count++; // ;
            }
        } else if (token.tk.equals("RETURNTK")) {
            CompUnit.count++;
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            }
            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'i');
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
                }
            }
            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'j');
            } else {
                CompUnit.count++; // )
            }

            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'i');
            } else {
                CompUnit.count++; // ;
            }
        } else {
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            }
            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'i');
            } else {
                CompUnit.count++; // ;
            }
        }

        Tools.WriteLine(Syntax.NodeType.Stmt, Tools.GetNowTK().id);
    }
}
