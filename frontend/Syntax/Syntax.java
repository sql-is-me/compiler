package frontend.Syntax;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import frontend.Lexer.Lexer;
import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Children.CompUnit;

public class Syntax {

    public class Node {
        String name;

        Node(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "<" + name + ">\n";
        }
    }

    public enum NodeType {
        CompUnit,
        Decl,
        ConstDecl,
        BType,
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
        BlockItem,
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
    }

    // public static Map<NodeType, String> nodeType = new HashMap<NodeType, String>() {
    //     {
    //         put(NodeType.CompUnit, "CompUnit");
    //         put(NodeType.Decl, "Decl");
    //         put(NodeType.ConstDecl, "ConstDecl");
    //         put(NodeType.BType, "BType");
    //         put(NodeType.ConstDef, "ConstDef");
    //         put(NodeType.ConstInitVal, "ConstInitVal");
    //         put(NodeType.VarDecl, "VarDecl");
    //         put(NodeType.VarDef, "VarDef");
    //         put(NodeType.InitVal, "InitVal");
    //         put(NodeType.FuncDef, "FuncDef");
    //         put(NodeType.MainFuncDef, "MainFuncDef");
    //         put(NodeType.FuncType, "FuncType");
    //         put(NodeType.FuncFParams, "FuncFParams");
    //         put(NodeType.FuncFParam, "FuncFParam");
    //         put(NodeType.Block, "Block");
    //         put(NodeType.BlockItem, "BlockItem");
    //         put(NodeType.Stmt, "Stmt");
    //         put(NodeType.Exp, "Exp");
    //         put(NodeType.Cond, "Cond");
    //         put(NodeType.LVal, "LVal");
    //         put(NodeType.PrimaryExp, "PrimaryExp");
    //         put(NodeType.Number, "Number");
    //         put(NodeType.UnaryExp, "UnaryExp");
    //         put(NodeType.UnaryOp, "UnaryOp");
    //         put(NodeType.FuncRParams, "FuncRParams");
    //         put(NodeType.MulExp, "MulExp");
    //         put(NodeType.AddExp, "AddExp");
    //         put(NodeType.RelExp, "RelExp");
    //         put(NodeType.EqExp, "EqExp");
    //         put(NodeType.LAndExp, "LAndExp");
    //         put(NodeType.LOrExp, "LOrExp");
    //         put(NodeType.ConstExp, "ConstExp");
    //     }
    // };

    private static List<String> parser = new ArrayList<String>(); // ans array

    public static List<String> getParser() { // get
        return parser;
    }

    public void SyntaxAnalysis() { // 启动分析
        CompUnit.CompUnitAnalysis();
    }
}
