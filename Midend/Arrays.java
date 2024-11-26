package Midend;

import java.util.ArrayList;

public class Arrays extends LLVMStruct {
    public ArrayList<Integer> values;
    public Integer size;

    public Arrays(String type, ArrayList<Integer> values) {
        this.values = values;
        this.size = values.size();
        super(type);
    }

    enum FuncTypes {
        IntArray,
        CharArray,
        ConstIntArray,
        ConstCharArray
    }
}
