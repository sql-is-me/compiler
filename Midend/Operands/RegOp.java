package Midend.Operands;

public class RegOp extends Operands {
    public Integer regNo;

    public RegOp(Integer regNo, Integer type, Boolean isArray, Boolean needNegative, boolean needNot) {
        super(type, isArray, needNegative, needNot);
        this.regNo = regNo;
    }
}
