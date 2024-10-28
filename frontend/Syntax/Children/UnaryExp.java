package frontend.Syntax.Children;

import java.util.ArrayList;

import SymbolTable.utils;
import SymbolTable.VarSymbol.VarTypes;
import frontend.ErrorLog;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Syntax;
import frontend.Pair;

public class UnaryExp {
    @SuppressWarnings("unchecked")
    static void UnaryExpAnalysis() {
        Pair pair = null;
        int funcRParamsNumber = 0;
        ArrayList<VarTypes> funcParamsTypes = null;
        Token token = Tools.LookNextTK();
        Token funcNameToken;
        if (token.tk.equals("IDENFR") && Tools.GetCountTK(CompUnit.count + 2).tk.equals("LPARENT")) {
            CompUnit.count++;// ident

            funcNameToken = Tools.GetNowTK();
            utils.JudgeUndefined(funcNameToken);

            CompUnit.count++; // (
            if (Tools.LookNextTK().tk.equals("LPARENT") || Tools.LookNextTK().tk.equals("INTCON")
                    || Tools.LookNextTK().tk.equals("CHRCON") || Tools.LookNextTK().tk.equals("IDENFR")
                    || Tools.LookNextTK().tk.equals("PLUS") || Tools.LookNextTK().tk.equals("MINU")
                    || Tools.LookNextTK().tk.equals("NOT")) {
                pair = FuncRParams.FuncRParamsAnalysis();
            }

            if (pair != null) {
                if (pair.a instanceof Integer && pair.b instanceof ArrayList) {
                    funcRParamsNumber = (int) pair.a;
                    funcParamsTypes = (ArrayList<VarTypes>) pair.b;
                } else {
                    System.out.println("pair转换类型不匹配");
                }
            }

            utils.JudgeFuncRParamsCorrect(funcNameToken, funcRParamsNumber, funcParamsTypes);

            if (!Tools.LookNextTK().tk.equals("RPARENT")) { // )
                Token temp = Tools.GetNowTK();
                ErrorLog.makelog_error(temp.line, 'j');
            } else {
                CompUnit.count++; // )
            }

        } else if (token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("NOT")) {
            UnaryOp.UnaryOpAnalysis();
            UnaryExpAnalysis();

        } else if (token.tk.equals("LPARENT") || token.tk.equals("INTCON")
                || token.tk.equals("CHRCON") || token.tk.equals("IDENFR")) { // (,number,character,ident
            PrimaryExp.PrimaryExpAnalysis();
        }

        Tools.WriteLine(Syntax.NodeType.UnaryExp, Tools.GetNowTK().id);
    }
}
