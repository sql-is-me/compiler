package Operands;

public class RegOp extends Operands {
    public Integer regNo;

    public RegOp(Integer regNo, Integer type, Boolean isArray, Boolean needNegative) {
        super(type, isArray, needNegative);
        this.regNo = regNo;
    }
}
