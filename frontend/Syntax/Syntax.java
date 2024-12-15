package Frontend.Syntax;

import java.util.List;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Children.CompUnit;

import java.util.ArrayList;

public class Syntax {

    public enum NodeType {
        CompUnit,
        ConstDecl,
        ConstDef,
        ConstInitVal,
        VarDecl,
        VarDef,
        InitVal,
        FuncDef,
        MainFuncDef,
        FuncType,
        FuncFParams,
        FuncFParam,
        Block,
        Stmt,
        Exp,
        Cond,
        LVal,
        PrimaryExp,
        Number,
        Character,
        UnaryExp,
        UnaryOp,
        FuncRParams,
        MulExp,
        AddExp,
        RelExp,
        EqExp,
        LAndExp,
        LOrExp,
        ConstExp,
        ForStmt
    }

    private static List<String> parser = new ArrayList<>(); // ans array
    private static List<Node> nodes = new ArrayList<>(); // node array

    public static List<Node> getNodes() { // get
        return nodes;
    }

    public static List<String> getParser() { // get
        return parser;
    }

    private static void setParser(List<Token> Lexer) {
        for (Token token : Lexer) {
            if (token.status) {
                parser.add(token.toString());
            }
        }
    }

    public static int SyntaxAnalysis() { // 启动分析
        setParser(Frontend.Lexer.Lexer.tokens);
        return CompUnit.CompUnitAnalysis();
    }
}
