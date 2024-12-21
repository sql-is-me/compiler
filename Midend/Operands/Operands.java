package Midend.Operands;

import java.util.Stack;

public class Operands {
    public Integer type;// 8 char 32 int
    public Boolean isArray;
    public Stack<Character> opStack;

    public Operands(Integer type, Boolean isArray, Stack<Character> opStack) {
        this.type = type;
        this.isArray = isArray;
        this.opStack = opStack;
    }
}
