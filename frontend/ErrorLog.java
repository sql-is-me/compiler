package frontend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ErrorLog {
    private int line;
    private char errortype;

    ErrorLog(int LineNum, char ErrorType) {
        this.line = LineNum;
        this.errortype = ErrorType;
    }

    @Override
    public String toString() {
        return line + " " + errortype + "\n";
    }

    private static String errorfile = "error.txt";

    private static List<ErrorLog> errorlog = new ArrayList<>();

    public static void makelog_error(int LineNum, char ErrorType) {
        ErrorLog log = new ErrorLog(LineNum, ErrorType);
        errorlog.add(log);
    }

    public static void WriteErrorLogs() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(errorfile))) {
            errorlog.sort((e1, e2) -> Integer.compare(e1.line, e2.line));
            for (ErrorLog error : errorlog) {
                bw.write(error.toString());
            }
        } catch (IOException e) {
            System.err.println("could not open + " + errorfile);
        }
    }
}
