package Operands;

public class RegOp extends Operands {
    Integer regNo;

    public RegOp(Integer regNo, boolean needNegative) {
        super(needNegative);
        this.regNo = regNo;
    }
}
