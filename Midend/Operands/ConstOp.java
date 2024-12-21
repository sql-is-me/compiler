package Midend.Operands;

import java.util.Stack;

public class ConstOp extends Operands {
    public int value;

    public ConstOp(int value,Stack<Character> opStack) {
        super(32, false,opStack);
        this.value = value;
    }
}
