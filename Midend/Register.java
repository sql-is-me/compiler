import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Register {
    List<Integer> stackReg;
    List<Integer> valueReg;
    List<Integer> constValue;

    Boolean isArray;
    Integer pointerReg; // 数组指针寄存器

    public Register(int size, boolean isArray) {// TODO:尝试中端优化，未使用的符号不分配寄存器
        if (size == -1) {
            size = 1;
        }

        this.stackReg = new ArrayList<>(Collections.nCopies(size, -1));
        this.valueReg = new ArrayList<>(Collections.nCopies(size, -1));
        this.constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
        this.pointerReg = -1;
        if (isArray) {
            this.isArray = true;
        } else {
            this.isArray = false;
        }
    }

    /**
     * 分配对应栈寄存器
     * 
     * @param sReg
     * @param pos  pos为-1时，分配第一个栈寄存器
     */
    public void allocStackReg(int sReg, int pos) {
        if (pos == -1) {
            pos = 0;
        }
        stackReg.add(pos, sReg);
    }

    /**
     * 检验栈寄存器是否已经被分配
     * 
     * @param pos pos为-1时，检验第一个栈寄存器是否被分配
     * @return
     */
    public boolean haveStackReg(int pos) {
        if (pos == -1)
            pos = 0;

        return stackReg.get(pos) != -1;
    }

    /**
     * 获取对应位置栈寄存器编号
     * 
     * @param pos pos为-1时，获取第一个栈寄存器编号
     * @return
     */
    public int getStackReg(int pos) {
        if (pos == -1)
            pos = 0;

        return stackReg.get(pos);
    }

    /**
     * 分配对应数组指针寄存器
     * 
     * @param pReg
     */
    public void allocPointReg(int pReg) {
        pointerReg = pReg;
    }

    /**
     * 获取对应数组指针寄存器编号
     * 
     * @param pos pos为-1时，获取第一个栈寄存器编号
     * @return
     */
    public int getPointReg() {
        return pointerReg;
    }

    /**
     * 初始化对应位置值寄存器
     * 
     * @param pos pos为-1时，初始化第一个值寄存器
     */
    public void initValueReg(int pos) {
        if (pos == -1) {
            pos = 0;
        }

        valueReg.set(pos, -1);
    }

    /**
     * 初始化对应位置常值
     * 
     * @param pos pos为-1时，初始化第一个常值
     */
    public void initConstValue(int pos) {
        if (pos == -1) {
            pos = 0;
        }

        constValue.set(pos, Integer.MIN_VALUE);
    }

    /**
     * 分配对应值寄存器
     * 
     * @param vReg
     * @param pos  pos为-1时，分配给第一个值寄存器
     */
    public void allocValueReg(int vReg, int pos) {
        if (pos == -1) {
            pos = 0;
        }

        initConstValue(pos);
        valueReg.set(pos, vReg);
    }

    /**
     * 分配对应常值,并初始化值寄存器
     * 
     * @param vReg
     * @param pos  pos为-1时，分配给第一个值寄存器
     */
    public void allocConstValue(int value, int pos) {
        if (pos == -1) {
            pos = 0;
        }

        constValue.set(pos, value);
        initValueReg(pos);
    }

    /**
     * 检验值寄存器是否已经被分配
     * 
     * @param pos pos为-1时，检验第一个值寄存器是否被分配
     * @return
     */
    public boolean haveValueReg(int pos) {
        if (pos == -1)
            pos = 0;

        return valueReg.get(pos) != -1;
    }

    /**
     * 检验是否有常值
     * 
     * @param pos pos为-1时，检验第一个位置是否有常值
     * @return
     */
    public boolean haveConstValue(int pos) {
        if (pos == -1)
            pos = 0;

        return constValue.get(pos) != Integer.MIN_VALUE;
    }

    /**
     * 获取对应位置值寄存器编号
     * 
     * @param pos pos为-1时，获取第一个值寄存器编号
     * @return
     */
    public int getValueReg(int pos) {
        if (pos == -1)
            pos = 0;

        return valueReg.get(pos);
    }

    /**
     * 获取对应位置常值
     * 
     * @param pos pos为-1时，获取第一个值寄存器编号
     * @return
     */
    public int getConstValue(int pos) {
        if (pos == -1)
            pos = 0;

        return constValue.get(pos);
    }
}
