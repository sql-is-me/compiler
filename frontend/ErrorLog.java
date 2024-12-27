package Frontend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ErrorLog {
    public int line;
    public char errortype;

    ErrorLog(int LineNum, char ErrorType) {
        this.line = LineNum;
        this.errortype = ErrorType;
    }

    @Override
    public String toString() {
        return line + " " + errortype + "\n";
    }

    private static String errorfile = "error.txt";

    private static List<ErrorLog> errorLog = new ArrayList<>();

    public static void makelog_error(int LineNum, char ErrorType) {
        ErrorLog log = new ErrorLog(LineNum, ErrorType);
        errorLog.add(log);
    }

    public static Boolean WriteErrorLogs() {
        if (errorLog.size() == 0)
            return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(errorfile))) {
            errorLog.sort((e1, e2) -> Integer.compare(e1.line, e2.line));
            for (ErrorLog error : errorLog) {
                bw.write(error.toString());
            }
        } catch (IOException e) {
            System.err.println("could not open + " + errorfile);
        }

        return true;
    }

    public static List<ErrorLog> GetErrorLog() {
        return errorLog;
    }

    public static void SetErrorLog(List<ErrorLog> oldErrorLog) {
        errorLog = oldErrorLog;
    }

    public static boolean isLastLineEqual(int linenum) {
        if (errorLog.isEmpty()) {
            return false;
        }
        if (linenum == errorLog.get(errorLog.size() - 1).line) {
            return true;
        } else {
            return false;
        }
    }
}
