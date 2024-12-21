package Midend.Operands;

import java.util.Stack;

public class RegOp extends Operands {
    public Integer regNo;

    public RegOp(Integer regNo, Integer type, Boolean isArray, Stack<Character> opStack) {
        super(type, isArray, opStack);
        this.regNo = regNo;
    }
}
