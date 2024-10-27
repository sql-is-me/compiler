package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;
import frontend.Syntax.Node;
import frontend.Syntax.Syntax;
import frontend.Syntax.Syntax.NodeType;
import SymbolTable.VarSymbol.VarTypes;
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

    public static void AddVarSymbol(boolean isConst, String btype, ThreePart tp) { // wait for add more parameter
        if (isConst) {
            if (btype.equals("Int")) {
                if (tp.isArray) {
                    utils.addVarSymbol(tp.name, VarTypes.ConstIntArray, 0, null);
                } else {
                    utils.addVarSymbol(tp.name, VarTypes.ConstInt, 0, null);
                }
            } else {
                if (tp.isArray) {
                    utils.addVarSymbol(tp.name, VarTypes.ConstCharArray, 0, null);
                } else {
                    utils.addVarSymbol(tp.name, VarTypes.ConstChar, 0, null);
                }
            }
        } else {
            if (btype.equals("Int")) {
                if (tp.isArray) {
                    utils.addVarSymbol(tp.name, VarTypes.IntArray, 0, null);
                } else {
                    utils.addVarSymbol(tp.name, VarTypes.Int, 0, null);
                }
            } else {
                if (tp.isArray) {
                    utils.addVarSymbol(tp.name, VarTypes.CharArray, 0, null);
                } else {
                    utils.addVarSymbol(tp.name, VarTypes.Char, 0, null);
                }
            }
        }
    }

    public static void AddFuncSymbol(FuncPart fp) { // wait for add more parameter
        utils.addFuncSymbol(fp.name, fp.returnType, fp.paramTypes, fp.paramNumber);
    }
}
