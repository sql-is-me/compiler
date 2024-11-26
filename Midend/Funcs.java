package Midend;

import java.util.ArrayList;
import java.util.Map;

public class Funcs extends LLVMStruct {
    public ArrayList<LLVMStruct> params;

    public Funcs(String type, Map<String, ArrayList<Integer>> params) {
        this.params = new ArrayList<>();

        
        for (String key : params.keySet()) {
            
        }
        super(type);
    }

    enum FuncTypes {
        IntFunc,
        CharFunc,
        VoidFunc
    }
}
