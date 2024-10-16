package frontend.Syntax;

import java.util.List;
import java.util.ArrayList;

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

    private static List<String> parser = new ArrayList<String>(); // ans array

    public static List<String> getParser() { // get
        return parser;
    }

    public void SyntaxAnalysis() { // 启动分析
        CompUnit.CompUnitAnalysis();
    }
}
