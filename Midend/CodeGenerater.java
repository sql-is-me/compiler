package Midend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import Frontend.Pair;
import Midend.Operands.ConstOp;
import Midend.Operands.Operands;
import Midend.Operands.RegOp;
import SymbolTable.FuncSymbol;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class CodeGenerater {
    /** llcode代码 */
    public static ArrayList<String> llcode = new ArrayList<String>();

    public static Boolean needTab = false;

    /**
     * 新加入一行代码
     * 
     * @param code
     */
    public static void addCodeatLast(String code) {
        if (needTab) {
            llcode.add("\t" + code + "\n");
        } else {
            llcode.add(code + "\n");
        }
    }

    public static void addLabelCode(String code) {
        llcode.add(code + "\n");
    }

    /**
     * 在初始新加入一行代码，用以追加全局字符串
     * 
     * @param code
     */
    public static void addCodeatFirst(String code) {
        llcode.add(0, code + "\n");
    }

    public static void addLibFuncs() {
        addCodeatLast("declare i32 @getint()");
        addCodeatLast("declare i32 @getchar()");
        addCodeatLast("declare void @putint(i32)");
        addCodeatLast("declare void @putch(i32)");
        addCodeatLast("declare void @putstr(i8*)\n");
    }

    /**
     * 分配全局变量
     * 
     * @param name  符号名
     * @param type  符号类型 32：int 8：char
     * @param value 值
     */
    public static void declareGloVar(String name, int type, int value) {
        StringBuilder sb = new StringBuilder();

        sb.append("@" + name + " = dso_local global ");

        if (type == 32) {
            sb.append("i32 ");
        } else if (type == 8) {
            sb.append("i8 ");
        }

        sb.append(value);

        addCodeatLast(sb.toString());
    }

    /**
     * 分配全局数组
     * 
     * @param name  数组名
     * @param type  符号类型 32：int 8：char
     * @param size  数组大小
     * @param value 值
     */
    public static void declareGloArr(String name, int type, int size, ArrayList<Integer> value,
            boolean needInitializer) {
        StringBuilder sb = new StringBuilder();

        sb.append("@" + name + " = dso_local global ");

        if (type == 32) {
            sb.append("[" + size + " x i32] ");
        } else if (type == 8) {
            sb.append("[" + size + " x i8] ");
        }
        if (needInitializer) {
            sb.append("zeroinitializer");
        } else {
            for (Integer v : value) {
                sb.append("[i" + type + " " + v + ", ");
            }

            sb.deleteCharAt(sb.length() - 1);// 删除最后一个" "
            sb.setCharAt(sb.length() - 1, ']');
        }

        addCodeatLast(sb.toString());
    }

    /** 字符串计数器，用以防止字符串重复 */
    private static int strNum = 1;

    /**
     * 创建全局字符串
     * 
     * @param length
     * @param str
     * @return 字符串名
     */
    public static String CreatGloStr(String str, int length) {
        StringBuilder sb = new StringBuilder();

        String name = "@.str." + strNum++;

        sb.append(name + " = private unnamed_addr constant [");

        sb.append(length + " x i8] c\"");

        sb.append(str + "\"");

        addCodeatLast(sb.toString());
        return name;
    }

    public static Integer CreatCalExp(boolean leftisConst, Integer left, boolean rightisConst, Integer right,
            Character op) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + retRegNO + " = ");

        switch (op) {
            case '*':
                sb.append("mul ");
                break;
            case '/':
                sb.append("sdiv ");
                break;
            case '%':
                sb.append("srem ");
                break;
            case '+':
                sb.append("add ");
                break;
            case '-':
                sb.append("sub ");
                break;
            default:
                break;
        }

        if (leftisConst) {
            sb.append("i32 " + left + ", ");
        } else {
            sb.append("i32 %" + left + ", ");
        }

        if (rightisConst) {
            sb.append(right);
        } else {
            sb.append("%" + right);
        }

        addCodeatLast(sb.toString());

        return retRegNO;
    }

    public static Integer CreatNegativeCode(Integer vReg) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + retRegNO + " = mul i32 -1, %" + vReg);

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    public static Integer CreatNotCode(Integer vReg) { // FIXME 逻辑非
        StringBuilder sb = new StringBuilder();
        Integer tempRegNO = utils.getRegNum();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + tempRegNO + " = icmp ne i32 %" + vReg + ", 0");
        sb.append("% " + retRegNO + " = xor i1 %" + tempRegNO + ", 1");

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    /**
     * 创建函数调用代码
     * 
     * @param funcSymbol
     * @param params
     * @return
     */
    public static Integer CreatCallFunc(FuncSymbol funcSymbol, ArrayList<Operands> params) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + retRegNO + " = call ");
        if (funcSymbol.returnType == FuncTypes.IntFunc) {
            sb.append("i32 ");
        } else if (funcSymbol.returnType == FuncTypes.CharFunc) {
            sb.append("i8 ");
        } else {
            sb.append("void ");
        }

        sb.append("@" + funcSymbol.name + "(");
        Operands t;
        for (int i = 0; i < params.size(); i++) {
            t = params.get(i);
            if (funcSymbol.paramTypes.get(i) == VarTypes.Int) {
                sb.append("i32 ");
            } else if (funcSymbol.paramTypes.get(i) == VarTypes.Char) {
                sb.append("i8 ");
            } else if (funcSymbol.paramTypes.get(i) == VarTypes.IntArray) {
                sb.append("i32* %");
            } else if (funcSymbol.paramTypes.get(i) == VarTypes.CharArray) {
                sb.append("i8* %");
            }

            if (t instanceof ConstOp) {
                sb.append(((ConstOp) t).value);
            } else {
                if ((t.type == 32 && funcSymbol.paramTypes.get(i) == VarTypes.Char)
                        || t.type == 8 && funcSymbol.paramTypes.get(i) == VarTypes.Int) {
                    t = CodeGenerater.CreatTransTypeCode(t);
                }
                sb.append("%" + ((RegOp) t).regNo);
            }

            if (i != params.size() - 1) {
                sb.append(", ");
            } else {
                sb.append(")");
            }
        }

        return retRegNO;
    }

    /**
     * 创建声明变量时，分配栈寄存器或数组指针寄存器代码
     * 
     * @param size >0为数组，0为变量
     * @param type 32：int 8：char
     * @return
     */
    public static Integer CreatAllocCode(Integer size, Integer type, Boolean isArrPointerInParams) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        if (isArrPointerInParams) {
            if (type == 32) {
                sb.append("%" + retRegNO + " = alloca i32*");
            } else {
                sb.append("%" + retRegNO + " = alloca i8*");
            }
        } else {
            if (size == 0) {
                if (type == 32) {
                    sb.append("%" + retRegNO + " = alloca i32");
                } else {
                    sb.append("%" + retRegNO + " = alloca i8");
                }
            } else {
                if (type == 32) {
                    sb.append("%" + retRegNO + " = alloca [" + size + " x i32]");
                } else {
                    sb.append("%" + retRegNO + " = alloca [" + size + " x i8]");
                }
            }
        }

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    public static Integer CreatLoadCode(Integer type, Boolean isGlobal, String sReg) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        if (isGlobal) {
            if (type == 32) {
                sb.append("%" + retRegNO + " = load i32, i32* @" + sReg);
            } else {
                sb.append("%" + retRegNO + " = load i8, i8* @" + sReg);
            }
        } else {
            if (type == 32) {
                sb.append("%" + retRegNO + " = load i32, i32* %" + sReg);
            } else {
                sb.append("%" + retRegNO + " = load i8, i8* %" + sReg);
            }
        }

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    /**
     * 创建store代码
     * 
     * @param type
     * @param isConst  存储值是否为常量
     * @param vReg
     * @param isGlobal 是否为全局变量
     * @param sReg
     */
    public static void CreatStoreCode(Integer type, Boolean isConst, Integer vReg, Boolean isGlobal,
            String sReg) {
        StringBuilder sb = new StringBuilder();

        if (isGlobal) {
            if (isConst) {
                if (type == 32) {
                    sb.append("store i32 " + vReg + ", i32* @" + sReg);
                } else {
                    sb.append("store i8 " + vReg + ", i8* @" + sReg);
                }
            } else {
                if (type == 32) {
                    sb.append("store i32 %" + vReg + ", i32* @" + sReg);
                } else {
                    sb.append("store i8 %" + vReg + ", i8* @" + sReg);
                }
            }
        } else {
            if (isConst) {
                if (type == 32) {
                    sb.append("store i32 " + vReg + ", i32* %" + sReg);
                } else {
                    sb.append("store i8 " + vReg + ", i8* %" + sReg);
                }
            } else {
                if (type == 32) {
                    sb.append("store i32 %" + vReg + ", i32* %" + sReg);
                } else {
                    sb.append("store i8 %" + vReg + ", i8* %" + sReg);
                }
            }
        }

        addCodeatLast(sb.toString());
    }

    public static void CreatStoreCode_Arr(Integer type, Integer sReg, Boolean isGlobal, String pReg) {
        StringBuilder sb = new StringBuilder();

        if (isGlobal) {
            if (type == 32) {
                sb.append("store i32* %" + sReg + ", i32** @" + pReg);
            } else {
                sb.append("store i8* %" + sReg + ", i8** @" + pReg);
            }
        } else {
            if (type == 32) {
                sb.append("store i32* %" + sReg + ", i32** %" + pReg);
            } else {
                sb.append("store i8* %" + sReg + ", i8** %" + pReg);
            }
        }

        addCodeatLast(sb.toString());
    }

    public static Integer CreatGetElementPtrCode_pReg(Integer size, Integer type, Boolean isGlobalArray,
            String pointerReg) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();
        sb.append("%" + retRegNO + " = getelementptr inbounds ");

        if (!isGlobalArray) { // 非全局数组
            if (size == 0) { // 函数调用时，不需要size
                if (type == 32) {
                    sb.append("i32, i32* %" + pointerReg + ", i32 ");
                } else {
                    sb.append("i8, i8* %" + pointerReg + ", i8 ");
                }
            } else {
                if (type == 32) {
                    sb.append("[" + size + " x i32], [" + size + " x i32]* %" + pointerReg + ", i32 0, i32 0");
                } else {
                    sb.append("[" + size + " x i8], [" + size + " x i8]* %" + pointerReg + ", i8 0, i8 0");
                }
            }
        } else {
            if (size == 0) { // 函数调用时，不需要size
                if (type == 32) {
                    sb.append("i32, i32* @" + pointerReg + ", i32 ");
                } else {
                    sb.append("i8, i8* @" + pointerReg + ", i8 ");
                }
            } else {
                if (type == 32) {
                    sb.append("[" + size + " x i32], [" + size + " x i32]* @" + pointerReg + ", i32 0, i32 0");
                } else {
                    sb.append("[" + size + " x i8], [" + size + " x i8]* @" + pointerReg + ", i8 0, i8 0");
                }
            }
        }

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    public static Integer CreatGetElementPtrCode_sReg(Integer type, Boolean posisReg, Integer pos, String sReg) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();
        sb.append("%" + retRegNO + " = getelementptr inbounds ");

        if (type == 32) {
            sb.append("i32, i32* %" + sReg + ", i32 ");
        } else {
            sb.append("i8, i8* %" + sReg + ", i8 ");
        }

        if (posisReg) {
            sb.append(" %" + pos);
        } else {
            sb.append(pos);
        }

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    public static Operands CreatTransTypeCode(Operands operands) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        if (operands.type == 8) { // char to int
            sb.append("%" + retRegNO + " = zext i8 %" + ((RegOp) operands).regNo + " to i32");
            addCodeatLast(sb.toString());
            return new RegOp(retRegNO, 32, operands.isArray, operands.needNegative, operands.needNot);
        } else { // int to char
            sb.append("%" + retRegNO + " = trunc i32 %" + ((RegOp) operands).regNo + " to i8");
            addCodeatLast(sb.toString());
            return new RegOp(retRegNO, 8, operands.isArray, operands.needNegative, operands.needNot);
        }
    }

    public static Integer CreatTransi32toi1Code(Operands operands) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + retRegNO + " = icmp ne i32 ");

        if (operands instanceof ConstOp) {
            sb.append(((ConstOp) operands).value);
        } else {
            sb.append("%" + ((RegOp) operands).regNo);
        }

        sb.append(", 0");

        return retRegNO;
    }

    public static void CreatFuncHeadCode(FuncSymbol funcSymbol) {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ");

        if (funcSymbol.returnType == FuncTypes.IntFunc) {
            sb.append("i32 ");
        } else if (funcSymbol.returnType == FuncTypes.CharFunc) {
            sb.append("i8 ");
        } else if (funcSymbol.returnType == FuncTypes.VoidFunc) {
            sb.append("void ");
        }

        sb.append("@" + funcSymbol.name + "(");

        utils.enterFuncBody();

        for (int i = 0; i < funcSymbol.paramTypes.size(); i++) {
            VarTypes vt = funcSymbol.paramTypes.get(i);
            if (vt == VarTypes.Int) {
                sb.append("i32 ");
            } else if (vt == VarTypes.Char) {
                sb.append("i8 ");
            } else if (vt == VarTypes.IntArray) {
                sb.append("i32* %");
            } else if (vt == VarTypes.CharArray) {
                sb.append("i8* %");
            }

            sb.append("%" + utils.getRegNum());

            if (i < funcSymbol.paramTypes.size() - 1) {
                sb.append(", ");
            } else {
                sb.append(") {");
            }
        }

        addCodeatLast(sb.toString());
        utils.setNeedTabTrue();
    }

    public static void CreatFuncEndCode() {
        utils.setNeedTabFalse();
        addCodeatLast("}");
        utils.quitFuncBody();
    }

    public static void CreatReturnCode(int retType, boolean isConst, int valueORvReg) {
        StringBuilder sb = new StringBuilder();
        if (retType == 0) { // void
            sb.append("ret void");
        } else {
            sb.append("ret i" + retType + " ");

            if (isConst) {
                sb.append(valueORvReg);
            } else {
                sb.append("%" + valueORvReg);
            }
        }

        addCodeatLast(sb.toString());
    }

    public static void CreatPrintfOperandsCode(int type, Operands operands) {
        StringBuilder sb = new StringBuilder();

        if (operands instanceof ConstOp) {
            ConstOp constOp = (ConstOp) operands;
            if (type == 32) {
                sb.append("call void @putint(i32 " + constOp.value + ")");
            } else {
                sb.append("call void @putch(i32 " + constOp.value + ")");
            }
        } else {
            RegOp regOp = (RegOp) operands;
            if (type == 32) {
                sb.append("call void @putint(i32 %" + regOp.regNo + ")");
            } else {
                sb.append("call void @putch(i32 %" + regOp.regNo + ")");
            }
        }

        addCodeatLast(sb.toString());
    }

    public static void CreatPrintfStringCode(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();

        if (length == 1) { // 防止在添加了\0前仅有一个字符
            CreatPrintfOperandsCode(8, new ConstOp(str.charAt(0), false, false));
            return;
        } else {
            String name = CreatGloStr(str, length);
            sb.append("call void @putstr(i8* getelementptr inbounds ([" + length
                    + " x i8], [" + length + " x i8]* " + name + ", i64 0, i64 0)");
        }

        addCodeatLast(sb.toString());
    }
    /*
     * —————————————————————————————————————————————————————————————————————————————
     */

    static Integer ifCount = 0;
    static Integer forCount = 0;
    static Integer count = 0;

    public static Stack<String> ifThenLabels = new Stack<>();
    public static Stack<String> elseLabels = new Stack<>();
    public static Stack<String> ifEndLabels = new Stack<>();

    public static Stack<String> forCondLabels = new Stack<>();
    public static Stack<String> forThenLabels = new Stack<>();
    public static Stack<String> forChangeLabels = new Stack<>();
    public static Stack<String> forEndLabels = new Stack<>();

    public static String getLabelNow() {
        return "label." + count++;
    }

    public static void CreatIfFirstLabelCode(Boolean haveElse) {
        StringBuilder sb = new StringBuilder();
        String ifTag = "if." + ifCount++;

        sb.append("br label %" + ifTag);
        addCodeatLast(sb.toString());

        sb = new StringBuilder();
        sb.append(ifTag + ":");
        addLabelCode(sb.toString());

        ifThenLabels.push("then." + ifTag);
        if (haveElse) {
            elseLabels.push("else." + ifTag);
        }
        ifEndLabels.push("end." + ifTag);
    }

    public static Integer CreatcalCondExp(boolean leftisConst, Integer left, boolean rightisConst, Integer right,
            String op) {// %cmp = icmp eq i32 %a, %b ne
        StringBuilder sb = new StringBuilder();
        int retReg = utils.getRegNum();
        sb.append("%" + retReg + " = icmp ");

        if (op.equals("==")) {
            sb.append("eq ");
        } else if (op.equals("!=")) {
            sb.append("ne ");
        } else if (op.equals("<")) {
            sb.append("slt ");
        } else if (op.equals(">")) {
            sb.append("sgt ");
        } else if (op.equals("<=")) {
            sb.append("sle ");
        } else if (op.equals(">=")) {
            sb.append("sge ");
        }

        if (leftisConst) {
            sb.append("i32 " + left + ", ");
        } else {
            sb.append("i32 %" + left + ", ");
        }

        if (rightisConst) {
            sb.append(right);
        } else {
            sb.append("%" + right);
        }

        addCodeatLast(sb.toString());

        retReg = utils.getRegNum();
        sb = new StringBuilder();
        sb.append("%" + retReg + " = zext i1 %" + (retReg - 1) + " to i32");
        addCodeatLast(sb.toString());

        return retReg;
    }

    public static Pair CreatShortJumpCode_And(int i1Reg, String trueDest, String falseDest) {
        StringBuilder sb = new StringBuilder();

        sb.append("br i1 %" + i1Reg + ", label %");

        if (falseDest == null) {
            falseDest = getLabelNow();
        }
        if (trueDest == null) {
            trueDest = getLabelNow();

            sb.append(trueDest + ", label %" + falseDest + '\n');
            addCodeatLast(sb.toString());

            sb = new StringBuilder();
            sb.append(trueDest + ":");
            addLabelCode(sb.toString());
        } else {
            sb.append(trueDest + ", label %" + falseDest + '\n');
            addCodeatLast(sb.toString());
        }

        return new Pair(trueDest, falseDest);
    }

    public static void CreatShortJumpCode_Or(String falseDest) {
        StringBuilder sb = new StringBuilder();

        sb.append(falseDest + ":");
        addLabelCode(sb.toString());

        if (falseDest != null) { // 跳转到else或者end
            sb = new StringBuilder();
            sb.append("br label %" + falseDest);
            addCodeatLast(sb.toString());
        }
    }

    public static void CreatLabelTagCode(String label) {
        StringBuilder sb = new StringBuilder();

        sb.append(label + ":");
        addLabelCode(sb.toString());
    }

    public static void CreatbrCode(String label) {
        StringBuilder sb = new StringBuilder();

        sb.append("br label %" + label);
        addLabelCode(sb.toString());
    }

    public static void CreatForFirstLabelCode(Boolean haveCond, Boolean haveChange) {
        StringBuilder sb = new StringBuilder();
        String forTag = "for." + forCount++;
        String starter;
        if (haveCond) {
            starter = "cond." + forTag;
            forCondLabels.push("cond." + forTag);

            sb.append("br label %" + starter);
            addCodeatLast(starter);
            sb = new StringBuilder();

            sb.append(starter + ":");
            addLabelCode(sb.toString());
        }
        if (haveChange) {
            forChangeLabels.push("change." + forTag);
        }

        forThenLabels.push("then." + forTag);
        forEndLabels.push("end." + forTag);
    }
    /*
     * —————————————————————————————————————————————————————————————————————————————
     */

    /**
     * 输出所有中间代码
     */
    public static void printfAllMidCodes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("llvm_ir.txt"))) {
            for (String s : llcode) {
                bw.write(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
