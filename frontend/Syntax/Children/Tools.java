package Frontend.Syntax.Children;

import SymbolTable.VarSymbol.VarTypes;
import SymbolTable.utils;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;
import Frontend.Syntax.Node;
import Frontend.Syntax.Syntax;
import Frontend.Syntax.Syntax.NodeType;

public class Tools {
    public static Token GetCountTK(int count) {
        return CompUnit.words.get(count);
    }

    public static Token GetNowTK() {
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

    /**
     * 通过调用utils来向符号表添加符号
     * 
     * @param isConst
     * @param btype
     * @param tp
     */
    public static void AddVarSymbol(boolean isConst, String btype, ThreePart tp) { // wait for add more parameter
        if (!utils.GetRepeat()) {
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
    }

    public static void AddFuncSymbol(FuncPart fp, boolean isFuncRepeat) { // wait for add more parameter
        if (!isFuncRepeat) {
            utils.addFuncSymbol(fp.name, fp.returnType, fp.paramTypes, fp.paramNumber);
        }
    }
}
