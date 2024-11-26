package Midend;

public class Vars extends LLVMStruct {
    public Integer value;

    public Vars(String type, int value)
    {
        this.value = value;
        super(type);
    }

    enum VarTypes {
        ConstInt,
        ConstChar,
        Int,
        Char
    }
}
