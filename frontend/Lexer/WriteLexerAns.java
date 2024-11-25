package Frontend.Lexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Frontend.Lexer.Lexer.Token;

public class WriteLexerAns {
    private static String lexerfile = "lexer.txt";

    public static void WriteAnswer(List<Token> tokens) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(lexerfile))) {
            for (Token token : tokens) {
                if (token.status) {
                    bw.write(token.toString());
                }

            }
        } catch (IOException e) {
            System.err.println("error when running lexer");
            System.err.println("could not open + " + lexerfile);
        }
    }
}