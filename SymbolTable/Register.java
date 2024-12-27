package SymbolTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Frontend.Pair;
import Midend.CodeGenerater;
import Midend.IterateTK;
import Midend.Operands.ConstOp;
import Midend.Operands.Operands;
import Midend.Operands.RegOp;

public class Register {
    public String pointerReg; // 数组指针寄存器
    public String stackReg;
    public List<Integer> valueReg;
    public List<Integer> constValue;

    public int type; // 32 8
    public Boolean isGlobal;
    public Boolean isConst;
    public Boolean isArray;
    public int size; // 数组长

    public Register(VarSymbol varSymbol, boolean isArray, int type) {
        if (varSymbol.size != -1) { // 定长数组
            if (varSymbol.size == 0) { // 变量初始化List为1即可
                this.size = 1;
            } else {
                this.size = varSymbol.size;
            }

            this.valueReg = new ArrayList<>(Collections.nCopies(size, -1));
            this.constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
        } else { // 不定长数组（函数参数）
            this.size = -1;
            this.valueReg = null;
            this.constValue = null;
        }

        this.pointerReg = "-1";
        this.stackReg = "-1";

        this.type = type;
        this.isArray = isArray;
        this.isGlobal = varSymbol.isGlobal;
        this.isConst = varSymbol.isConst;

        if (IterateTK.cur_symTab.equals(IterateTK.global_symTab)) { // 全局变量，直接命名名字即可
            if (isArray)
                pointerReg = varSymbol.name;
            else {
                stackReg = varSymbol.name;
            }
        } else { // 局部变量
            if (isArray) { // 直接调用alloc分配指针寄存器
                if (size == -1) {
                    this.pointerReg = CodeGenerater.CreatAllocCode(size, type, true).toString();
                } else {
                    this.pointerReg = CodeGenerater.CreatAllocCode(size, type, false).toString();
                }
            } else {
                Integer sReg = CodeGenerater.CreatAllocCode(0, type, false);
                stackReg = sReg.toString();
            }
        }
    }

    public Register(Register register) {
        this.pointerReg = register.pointerReg;
        this.stackReg = register.stackReg;
        if (register.valueReg != null) {
            this.valueReg = new ArrayList<>(Collections.nCopies(size, -1));
            this.constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
        } else {
            this.valueReg = register.valueReg;
            this.constValue = register.constValue;
        }
        this.type = register.type;
        this.isGlobal = register.isGlobal;
        this.isArray = register.isGlobal;
        this.size = register.size;
    }

    /* —————————————————————————————————————————————————————————————————————————— */

    /**
     * 获取值寄存器以用于计算
     * 
     * @param isReg
     * @param pos
     * @return
     */
    public Pair getValueReg(boolean isReg, int pos) {
        if (isArray && stackReg == "-1") { // 数组未分配栈寄存器，先分配
            this.stackReg = CodeGenerater.CreatGetElementPtrCode_pReg(size, type, isGlobal, pointerReg).toString();
        }

        if (size == -1) { // 函数参数的变长数组，无法确定len，故每次getReg直接进行代码生成，获取sReg和vReg
            Integer sReg = CodeGenerater.CreatGetElementPtrCode_sReg(type, isReg, pos, stackReg);
            Integer vReg = CodeGenerater.CreatLoadCode(type, false, sReg.toString());

            return new Pair(false, vReg);
        }

        // if (isArray && stackReg == "-1") { // 数组未分配栈寄存器，先分配
        // this.stackReg = CodeGenerater.CreatGetElementPtrCode_pReg(size, type,
        // isGlobal, pointerReg).toString();
        // }

        if (isReg) { // pos为寄存器时,一定是数组
            Integer sReg = CodeGenerater.CreatGetElementPtrCode_sReg(type, isReg, pos, stackReg);
            Integer vReg = CodeGenerater.CreatLoadCode(type, false, sReg.toString());

            return new Pair(false, vReg);
        } else { // pos 为常值
            if (haveConstValue(pos)) { // 有常值，直接取
                return new Pair(true, constValue.get(pos));
            } else if (haveValueReg(pos)) { // 获取值寄存器
                return new Pair(false, valueReg.get(pos));
            } else {
                Integer vReg;
                if (isArray) {
                    Integer sReg = CodeGenerater.CreatGetElementPtrCode_sReg(type, isReg, pos, stackReg);
                    vReg = CodeGenerater.CreatLoadCode(type, false, sReg.toString());
                } else {
                    vReg = CodeGenerater.CreatLoadCode(type, isGlobal, stackReg);
                }

                valueReg.set(pos, vReg);
                return new Pair(false, vReg);
            }
        }
    }

    public void initAllConstandValueReg() {
        if (size == -1) {
            valueReg = new ArrayList<>();
            constValue = new ArrayList<>();
        } else {
            if (!isConst) { // 非常量才需要初始化
                valueReg = new ArrayList<>(Collections.nCopies(size, -1));
                constValue = new ArrayList<>(Collections.nCopies(size, Integer.MIN_VALUE));
            }
        }
    }

    public void initValueReg(int pos) {
        valueReg.set(pos, -1);
    }

    public void initConstValue(int pos) {
        constValue.set(pos, Integer.MIN_VALUE);
    }

    public void initAllValueReg(int pos) {
        initConstValue(pos);
        initValueReg(pos);
    }

    public boolean haveValueReg(int pos) {
        return valueReg.get(pos) != -1;
    }

    public boolean haveConstValue(int pos) {
        return constValue.get(pos) != Integer.MIN_VALUE;
    }

    /**
     * 存储值寄存器
     * 
     * @param posisConst
     * @param pos
     * @param isConst
     * @param vORvReg
     * @param isArray
     */
    public void storeReg(boolean posisConst, int pos, Operands vOperand) {
        Boolean isConst;
        int vORvReg;
        vOperand = Midend.utils.JudgeOperandsType(vOperand, type);

        if (vOperand instanceof ConstOp) {
            isConst = true;
            vORvReg = ((ConstOp) vOperand).value;
        } else {
            isConst = false;
            vORvReg = ((RegOp) vOperand).regNo;
        }

        if (size == -1) { // 参数数组完全不知道多长以及存哪，所以直接初始化即可
            initAllConstandValueReg();
        } else {
            if (posisConst) {
                if (isConst) {
                    if (type == 8) {
                        vORvReg = vORvReg & 0xff;
                    }
                    constValue.set(pos, vORvReg);
                    initValueReg(pos);
                } else { // 存了一个寄存器值，那就直接初始化等待下次调用时分配即可
                    initAllValueReg(pos);
                }
            } else { // pos的值是一个寄存器
                initAllConstandValueReg(); // 不知道存哪，所以所有的都初始化
            }
        }

        if (isArray) {
            if (stackReg == "-1") { // 以防全局数组未分配对应栈寄存器，先分配
                this.stackReg = CodeGenerater.CreatGetElementPtrCode_pReg(size, type, isGlobal, pointerReg).toString();
            }
            Integer sReg = CodeGenerater.CreatGetElementPtrCode_sReg(type, !posisConst, pos, stackReg);
            CodeGenerater.CreatStoreCode(type, isConst, vORvReg, false, sReg.toString()); // 数组指针赋值，故false
        } else { // 存值寄存器（非数组）
            CodeGenerater.CreatStoreCode(type, isConst, vORvReg, isGlobal, stackReg);
        }
    }

    public void initStoreArrReg(String sReg) { // 不会出现将数组指针赋给数组的情况
        CodeGenerater.CreatStoreCode_Arr(type, Integer.parseInt(sReg), isGlobal, pointerReg);
        stackReg = CodeGenerater.CreatLoadCode_initArrParam(type, isGlobal, pointerReg).toString(); // 初始化数组参数(将数组指针赋给数组)
    }

    public String getArrPointer() {
        if (stackReg == "-1") { // 数组未分配栈寄存器，先分配
            stackReg = CodeGenerater.CreatGetElementPtrCode_pReg(size, type, isGlobal, pointerReg).toString();
        }
        return stackReg;
    }
}
