package Operands;

public class ConstOp extends Operands {
    public int value;

    public ConstOp(int value, boolean needNegative) {
        super(32, false, needNegative);
        this.value = value;
    }
}
