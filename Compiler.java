import Frontend.ErrorLog;
import Frontend.Lexer.*;
import Frontend.Syntax.*;
import Midend.MidCodeGenerate;
import SymbolTable.Symbol;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    public static void main(String[] args) {
        final String fString = "testfile.txt"; // file name and path

        try (BufferedReader filebr = new BufferedReader(new FileReader(fString))) {
            Lexer.lexer(filebr);
            // WriteLexerAns.WriteAnswer(Lexer.tokens);

            int mainOffset = Syntax.SyntaxAnalysis();
            // WriteSyntaxAns.WriteAnswer(Syntax.getParser(), Syntax.getNodes());

            Symbol.VisitAllSymbolTable();
            // WriteSymbolAns.WriteAnwser();

            if (ErrorLog.someThingWrong()) {
                ErrorLog.WriteErrorLogs();// 输出错误日志
            } else {
                MidCodeGenerate.generateMidCode(mainOffset);
                MidCodeGenerate.printfCodetoLL();
            }

        } catch (FileNotFoundException fnfe) {
            System.err.println("file not found + " + fString);
        } catch (IOException IOe) {
            System.err.println("IOException");
        }
    }
}