package frontend.Syntax.Children;

import frontend.Lexer.Lexer.Token;

import java.util.ArrayList;

public class UnaryExp {
    public static ArrayList<Token> tokenArr;
    public static int count = 0;

    static void UnaryExpAnalysis(ArrayList<Token> arr) {
        tokenArr = arr;

        while (count < arr.size()) {
            Token token = arr.get(count);
            if (token.tk.equals("LPARENT")) { // (
                PrimaryExp.PrimaryExpAnalysis();
            } else if (token.tk.equals("IDENFR")) {
                count++;
                token = arr.get(count);
                if (token.tk.equals("IDENFR")) {
                    // FuncRParams
                }
            } else if (token.tk.equals("PLUS") || token.tk.equals("MINU") || token.tk.equals("NOT")) {
                UnaryOp.UnaryOpAnalysis();

                count++;
                ArrayList<Token> unaryexparr = new ArrayList<>();
                while (count < arr.size()) {
                    token = arr.get(count);
                    unaryexparr.add(token);
                    count++;
                }
                UnaryExpAnalysis(unaryexparr);
            }
        }
    }
}
