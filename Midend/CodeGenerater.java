import java.util.ArrayList;

public class CodeGenerater {
    /** llcode代码 */
    ArrayList<String> llcode = new ArrayList<String>();

    /**
     * 新加入一行代码
     * 
     * @param code
     */
    public void addCodeatLast(String code) {
        llcode.add(code + "\n");
    }

    /**
     * 在初始新加入一行代码，用以追加全局字符串
     * 
     * @param code
     */
    public void addCodeatFirst(String code) {
        llcode.add(0, code + "\n");
    }
}
