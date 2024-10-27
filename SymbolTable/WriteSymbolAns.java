package SymbolTable;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class WriteSymbolAns {
    public static ArrayList<String> ansArray;

    public static void WriteAnwser() {
        ansArray = utils.WriteAnstoArray();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("symbol.txt"))) {
            for (String str : ansArray) {
                bw.write(str);
            }
        } catch (IOException e) {
            System.err.println("error when running parser");
            System.err.println("could not open + symbol.txt");
        }
    }

}
