package frontend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ErrorLog {
    private static String errorfile = "error.txt";

    private static List<String> errorlog = new ArrayList<String>();

    public static void makelog_error(int LineNum, char ErrorType) {
        StringBuilder temp = new StringBuilder();
        temp.append(LineNum);
        temp.append(" " + ErrorType + "\n");
        String log = temp.toString();

        errorlog.add(log);
    }

    public static void WriteErrorLogs() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(errorfile))) {
            for (String error : errorlog) {
                bw.write(error.toString());
            }
        } catch (IOException e) {
            System.err.println("could not open + " + errorfile);
        }
    }
}
