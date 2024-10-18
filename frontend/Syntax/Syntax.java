package frontend.Syntax;

import java.util.List;
import java.util.ArrayList;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Children.CompUnit;

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

    private static void setParser(List<Token> Lexer) {
        for (Token token : Lexer) {
            parser.add(token.toString());
        }
    }

    public void SyntaxAnalysis() { // 启动分析
        setParser(frontend.Lexer.Lexer.tokens);
        CompUnit.CompUnitAnalysis();
        WriteSyntaxAns.WriteAnswer(parser, nodes);
    }
}
