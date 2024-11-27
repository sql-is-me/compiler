package Midend;

public class RegisterManager {
    /** 寄存器编号 */
    public int regNO;
    /** 函数编号 */
    public int funcID;

    public RegisterManager(int funcID) {
        this.regNO = 0;
        this.funcID = funcID;
    }
}
