package Midend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MidCodeGenerate {
    /** 存储中端代码 */
    public static ArrayList<String> midcode = new ArrayList<>();

    public static void generateMidCode() {
        M_utils.addGlobalVarandFunc();
    }

    public static void addLinetoAns(String code) {
        midcode.add(code);
    }

    public static void printfCodetoLL() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("llvm_ir.txt"))) {
            bw.write("declare i32 @getint()\n" +
                    "declare i32 @getchar()\n" +
                    "declare void @putint(i32)\n" +
                    "declare void @putch(i32)\n" +
                    "declare void @putstr(i8*)\n");
            for (String s : midcode) {
                bw.write(s);
            }
        } catch (IOException e) {
            System.err.println("could not open llvm_ir.txt");
        }
    }

}
