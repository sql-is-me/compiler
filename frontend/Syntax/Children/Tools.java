package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Node;
import frontend.Syntax.Syntax;
import frontend.Syntax.Syntax.NodeType;
import SymbolTable.Symbol.TokenType;
import SymbolTable.utils;

import java.util.ArrayList;

public class Tools {
    public static Token GetCountTK(int count) {
        return CompUnit.words.get(count);
    }

    public static Token GetNowTK() {
        return CompUnit.words.get(CompUnit.count);
    }

    public static Token GetNextTK() { // 找到下一个TK
        CompUnit.count++;
        return CompUnit.words.get(CompUnit.count);
    }

    public static Token LookNextTK() { // 看下一个TK
        return CompUnit.words.get(CompUnit.count + 1);
    }

    public static void WriteLine(NodeType type, int index) {
        Node node = new Node(type, index);
        Syntax.getNodes().add(node);
    }

    public static ArrayList<Token> GetExpfromIndex(int begin, int end) {
        ArrayList<Token> al = new ArrayList<>();
        while (begin <= end) {
            al.add(CompUnit.words.get(begin));
            begin++;
        }
        return al;
    }

    public static void AddConstSymbol(String btype, ThreePart tp) { // wait for add more parameter
        if (btype.equals("Int")) {
            if (tp.isArray) {
                utils.addSymbol(tp.name, TokenType.ConstIntArray, null, 0, null);
            } else {
                utils.addSymbol(tp.name, TokenType.ConstInt, null, 0, null);
            }
        } else {
            if (tp.isArray) {
                utils.addSymbol(tp.name, TokenType.ConstCharArray, null, 0, null);
            } else {
                utils.addSymbol(tp.name, TokenType.ConstChar, null, 0, null);
            }
        }
    }

    public static void AddVarSymbol(String btype, ThreePart tp) { // wait for add more parameter
        if (btype.equals("Int")) {
            if (tp.isArray) {
                utils.addSymbol(tp.name, TokenType.IntArray, null, 0, null);
            } else {
                utils.addSymbol(tp.name, TokenType.Int, null, 0, null);
            }
        } else {
            if (tp.isArray) {
                utils.addSymbol(tp.name, TokenType.CharArray, null, 0, null);
            } else {
                utils.addSymbol(tp.name, TokenType.Char, null, 0, null);
            }
        }
    }

    public static void AddFuncSymbol(TokenType returnType, FuncPart fp) { // wait for add more parameter
        utils.addSymbol(fp.name, returnType, null, 0, fp.paramTypes);
    }
}
