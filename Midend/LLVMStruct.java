package Midend;

public class LLVMStruct {
    // public String name;
    public Integer registerNO;
    public String type;

    LLVMStruct(String type) {
        this.registerNO = M_utils.registerNO++;
        this.type = type;
    }
}
