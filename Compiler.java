import frontend.ErrorLog;
import frontend.Lexer.*;
import frontend.Syntax.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    public static void main(String[] args) {
        String fString = "testfile.txt"; // file name and path

        try (BufferedReader filebr = new BufferedReader(new FileReader(fString))) {
            Lexer lexer = new Lexer();
            lexer.lexer(filebr);

            Syntax syntax = new Syntax();
            syntax.SyntaxAnalysis();

            ErrorLog.WriteErrorLogs(); // 输出错误日志

        } catch (FileNotFoundException fnfe) {
            System.err.println("file not found + " + fString);
        } catch (IOException IOe) {
            System.err.println("IOException");
        }
    }
}