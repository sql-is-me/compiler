package Frontend.Syntax.Children;

import java.util.ArrayList;
import java.util.List;

import Frontend.ErrorLog;
import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Node;
import Frontend.Syntax.Syntax;
import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;

public class Stmt {
    private static Boolean tryLVal() {
        int count = CompUnit.count;
        List<Node> temp = new ArrayList<>(Syntax.getNodes());
        List<ErrorLog> oldErrorLog = new ArrayList<>(ErrorLog.GetErrorLog());

        LVal.LValAnalysis();
        if (Tools.LookNextTK().tk.equals("ASSIGN")) { // =
            CompUnit.count = count;
            Syntax.getNodes().clear();
            Syntax.getNodes().addAll(temp);
            ErrorLog.SetErrorLog(oldErrorLog);
            return true;
        } else {
            CompUnit.count = count;
            Syntax.getNodes().clear();
            Syntax.getNodes().addAll(temp);
            ErrorLog.SetErrorLog(oldErrorLog);
            return false;
        }
    }

    static void StmtAnalysis(boolean IForFORwithoutBlock) {
        Token token = Tools.LookNextTK();

        if (token.tk.equals("IDENFR") && (Tools.GetCountTK(CompUnit.count + 2).tk.equals("ASSIGN")
                || Tools.GetCountTK(CompUnit.count + 2).tk.equals("LBRACK"))) {
            if (tryLVal()) {
                VarTypes varType = LVal.LValAnalysis();
                utils.JudgeLValisConst(varType, Tools.GetNowTK().line);

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
            Block.BlockAnalysis(false);
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

                if (!Tools.LookNextTK().tk.equals("LBRACE")) {
                    utils.createSymTab(utils.curSymTab);
                    Stmt.StmtAnalysis(true);
                    utils.jumpOutSymTab();
                } else {
                    Stmt.StmtAnalysis(false);
                }

                if (Tools.LookNextTK().tk.equals("ELSETK")) {
                    CompUnit.count++; // else
                    if (!Tools.LookNextTK().tk.equals("LBRACE")) {
                        utils.createSymTab(utils.curSymTab);
                        Stmt.StmtAnalysis(true);
                        utils.jumpOutSymTab();
                    } else {
                        Stmt.StmtAnalysis(false);
                    }
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

            utils.Inloop();
            if (!Tools.LookNextTK().tk.equals("LBRACE")) {
                utils.createSymTab(utils.curSymTab);
                Stmt.StmtAnalysis(true);
                utils.jumpOutSymTab();
            } else {
                Stmt.StmtAnalysis(false);
            }
            utils.Outloop();

        } else if (token.tk.equals("BREAKTK") || token.tk.equals("CONTINUETK")) {
            CompUnit.count++; // break or continue
            utils.JudgeInLoop(Tools.GetNowTK());

            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                ErrorLog.makelog_error(tempToken.line, 'i');
            } else {
                CompUnit.count++; // ;
            }
        } else if (token.tk.equals("RETURNTK")) {
            CompUnit.count++;
            utils.JudgeReturnUnmatch(token, Tools.LookNextTK());

            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                Exp.ExpAnalysis();
            }

            if (!Tools.LookNextTK().tk.equals("SEMICN")) { // ;
                Token tempToken = Tools.GetNowTK();
                if (ErrorLog.GetErrorLog().get(ErrorLog.GetErrorLog().size() - 1).errortype == 'f'
                        && ErrorLog.GetErrorLog().get(ErrorLog.GetErrorLog().size() - 1).line == tempToken.line) {
                    ErrorLog.GetErrorLog().remove(ErrorLog.GetErrorLog().size() - 1); // 如果是f类错误，即voidreturn无;，删除
                }

                ErrorLog.makelog_error(tempToken.line, 'i');
            } else {
                CompUnit.count++; // ;
            }

            if (!IForFORwithoutBlock) {
                utils.findReturn(Tools.LookNextTK());
            }

        } else if (token.tk.equals("PRINTFTK")) {
            CompUnit.count++;
            Token printfToken = Tools.GetNowTK();
            if (Tools.LookNextTK().tk.equals("LPARENT")) {
                CompUnit.count++;
                if (Tools.LookNextTK().tk.equals("STRCON")) {
                    CompUnit.count++;
                    ArrayList<VarTypes> needParamsTypes = utils.AnalysisPrintString(Tools.GetNowTK());
                    ArrayList<VarTypes> paramsTypes = new ArrayList<>();
                    ArrayList<Token> expTokens;

                    while (Tools.LookNextTK().tk.equals("COMMA")) {
                        CompUnit.count++; // ,
                        expTokens = Exp.ExpAnalysis();
                        paramsTypes.add(VarTypes.valueOf(utils.JudgeExpType(expTokens)));
                    }

                    if (!paramsTypes.contains(VarTypes.Undefined)) {
                        utils.JudgePrintfParamsCorrect(printfToken, needParamsTypes, paramsTypes);
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
