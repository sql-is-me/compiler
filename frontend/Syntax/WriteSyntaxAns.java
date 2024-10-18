package frontend.Syntax;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteSyntaxAns {
    private static String parserfile = "parser.txt";

    public static void WriteAnswer(List<String> words, List<Node> nodes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(parserfile))) {
            for (int i = nodes.size() - 1; i >= 0; i--) {
                words.add(nodes.get(i).line + 1, nodes.get(i).toString());
            }

            for (String word : words) {
                bw.write(word);
            }
        } catch (IOException e) {
            System.err.println("error when running parser");
            System.err.println("could not open + " + parserfile);
        }
    }
}