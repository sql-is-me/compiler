package frontend.Syntax;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteLexerAns {
    private static String parserfile = "parser.txt";

    public static void WriteAnswer(List<String> words) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(parserfile))) {
            for (String word : words) {
                bw.write('<' + word + '>' + '\n');
            }
        } catch (IOException e) {
            System.err.println("error when running parser");
            System.err.println("could not open + " + parserfile);
        }
    }
}