package Operands;

public class ConstOp extends Operands {
    public int value;

    public ConstOp(int value, boolean needNegative) {
        super(needNegative);
        this.value = value;
    }
}
