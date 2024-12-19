package Midend.Operands;

public class Operands {
    public Boolean needNegative;
    public Boolean needNot;
    public Integer type;// 8 char 32 int
    public Boolean isArray;

    public Operands(Integer type, Boolean isArray, boolean needNegative, boolean needNot) {
        this.type = type;
        this.isArray = isArray;
        this.needNegative = needNegative;
        this.needNot = needNot;
    }
}
