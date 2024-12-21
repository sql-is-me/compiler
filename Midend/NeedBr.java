package Midend;

public class NeedBr {
    boolean haveReturn;
    boolean haveBreak;
    boolean haveContinue;

    public NeedBr(boolean a, boolean b, boolean c) {
        this.haveBreak = a;
        this.haveContinue = b;
        this.haveReturn = c;
    }

    public boolean JudgeNeedBr() {
        return haveBreak || haveContinue || haveReturn;
    }
}
