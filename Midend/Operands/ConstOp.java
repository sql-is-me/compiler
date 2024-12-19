package Midend.Operands;

public class ConstOp extends Operands {
    public int value;

    public ConstOp(int value, boolean needNegative, boolean needNot) {
        super(32, false, needNegative, needNot);
        this.value = value;
    }
}
