import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Frontend.Pair;

public class Register {
    String pointerReg; // 数组指针寄存器
    List<String> stackReg;
    List<Integer> valueReg;
    List<Integer> constValue;

    int type;
    Boolean isGlobal;
    Boolean isArray;
    int size;

    public Register(int size, boolean isArray, int type, boolean isGlobal) {
        if (size != -1) { // 定长数组
            if (size == 0) {
                size = 1;
            }
            this.stackReg = new ArrayList<>(Collections.nCopies(size, "-1"));
            this.valueReg = new ArrayList<>(Collections.nCopies(size, -1));
            this.constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
        } else { // 不定长数组（函数参数）
            this.stackReg = new ArrayList<>();
            this.valueReg = new ArrayList<>();
            this.constValue = new ArrayList<>();
        }

        this.pointerReg = "-1";
        this.type = type;
        this.size = size;
        if (isArray) {
            this.isArray = true;
            allocPointerReg();
        } else {
            this.isArray = false;
            allocStackReg();
        }
        this.isGlobal = isGlobal;
    }

    /**
     * 分配对应栈寄存器
     * 
     * @param type 32 int 8 char
     */
    private void allocStackReg() {
        Integer sReg = CodeGenerater.CreatAllocCode(0, type, false);
        stackReg.set(0, sReg.toString());
    }

    /**
     * 分配对应指针寄存器
     * 
     * @param size
     * @param type 32 int 8 char
     */
    private void allocPointerReg() {
        if (size == -1) { // 为函数中参数数组指针，无法确定其长度
            pointerReg = CodeGenerater.CreatAllocCode(size, type, true).toString();
        } else {
            pointerReg = CodeGenerater.CreatAllocCode(size, type, false).toString();
        }
    }

    /* —————————————————————————————————————————————————————————————————————————— */

    /**
     * 获取值寄存器以用于计算
     * 
     * @param pos    -2时代表是位置的值存储在一寄存器，直接生成代码即可
     * @param posReg
     * @return
     */
    public Pair getReg(int pos, int posReg) {
        if (size == -1) { // 函数参数的变长数组，无法确定len，故每次getReg直接进行代码生成，获取sReg和vReg
            if (pos == -2) { // -2为一寄存器
                Integer sReg = CodeGenerater.CreatGetelementptrCode(size, type, true, posReg, isGlobal,
                        pointerReg.toString());
                Integer vReg = CodeGenerater.CreatLoadCode(type, isGlobal, sReg.toString());
                return new Pair(false, vReg);
            } else { // pos为一常值
                Integer sReg = CodeGenerater.CreatGetelementptrCode(size, type, false, pos, isGlobal,
                        pointerReg.toString());
                Integer vReg = CodeGenerater.CreatLoadCode(type, isGlobal, sReg.toString());
                return new Pair(false, vReg);
            }
        }

        if (pos == -1) {
            pos = 0;
        } else if (pos == -2) { // pos为寄存器时，清空所有值和常值寄存器
            Integer sReg = CodeGenerater.CreatGetelementptrCode(size, type, true, posReg, isGlobal,
                    pointerReg.toString());
            Integer vReg = CodeGenerater.CreatLoadCode(type, isGlobal, sReg.toString());
            initAllConstandValueReg(); // 清空所有值和常值寄存器
            return new Pair(false, vReg);
        }

        if (haveConstValue(pos)) { // 有常值，直接取
            return new Pair(true, constValue.get(pos));
        } else if (haveStackReg(pos)) { // 有栈寄存器，尝试获取常值寄存器
            return new Pair(false, getValueReg(pos));
        } else { // 没有栈寄存器，需要先获取栈寄存器，再取值寄存器
            getStackReg(false, pos);
            return new Pair(false, getValueReg(pos));
        }
    }

    /**
     * 获取对应位置栈寄存器编号
     * 
     * @param isReg
     * @param pos
     * @return
     */
    public String getStackReg(boolean isReg, int pos) {
        if (!isReg) {
            if (!stackReg.get(pos).equals("-1")) { // 已经分配过
                return stackReg.get(pos);
            } else { // 若未分配过，则进行分配
                Integer sReg;
                if (isArray) {
                    sReg = CodeGenerater.CreatGetelementptrCode(size, type, false, pos, isGlobal,
                            pointerReg.toString());
                } else {
                    sReg = CodeGenerater.CreatAllocCode(0, type, false);
                }
                stackReg.set(pos, sReg.toString());
                return sReg.toString();
            }
        } else {
            Integer sReg;
            initAllConstandValueReg();

            sReg = CodeGenerater.CreatGetelementptrCode(size, type, true, pos, isGlobal, pointerReg.toString());
            return sReg.toString();
        }
    }

    public Integer getValueReg(int pos) {
        if (haveValueReg(pos)) {
            return valueReg.get(pos);
        } else { // 有栈但无值
            Integer vReg = CodeGenerater.CreatLoadCode(type, isGlobal, stackReg.get(pos));
            valueReg.set(pos, vReg);
            return vReg;
        }
    }

    public void initAllConstandValueReg() { // 指针寄存器发生改变时使用;
        valueReg = new ArrayList<>(Collections.nCopies(size, -1));
        constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
    }

    public void initValueReg(int pos) {
        valueReg.set(pos, -1);
    }

    public void initConstValue(int pos) {
        constValue.set(pos, Integer.MIN_VALUE);
    }

    public boolean haveStackReg(int pos) {
        return !stackReg.get(pos).equals("-1");
    }

    public boolean haveValueReg(int pos) {
        return valueReg.get(pos) != -1;
    }

    public boolean haveConstValue(int pos) {
        return constValue.get(pos) != Integer.MIN_VALUE;
    }

    public void storeReg_simple(boolean posisConst, int pos, boolean isConst, int vORvReg) {
        if (!posisConst) {
            if (isConst) {
                constValue.set(pos, vORvReg);
                initValueReg(pos);
            } else { // 存了一个寄存器值，那就直接初始化等待下次调用时分配即可
                initValueReg(pos);
            }
            CodeGenerater.CreatStoreCode_simple(type, isConst, vORvReg, isGlobal, stackReg.get(pos));
        } else { // pos的值是一个寄存器
            String sReg = getStackReg(true, pos);
            CodeGenerater.CreatStoreCode_simple(type, isConst, vORvReg, isGlobal, sReg);
        }
    }

    public void storeReg_Arr(String sReg) { // 不会出现将数组指针赋给数组的情况
        CodeGenerater.CreatStoreCode_Arr(type, Integer.parseInt(sReg), isGlobal, pointerReg);
        // initStackReg();
    }
}
