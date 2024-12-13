import java.util.ArrayList;

import Operands.Operands;
import SymbolTable.FuncSymbol;
import SymbolTable.FuncSymbol.FuncTypes;
import SymbolTable.VarSymbol.VarTypes;

public class CodeGenerater {
    /** llcode代码 */
    public static ArrayList<String> llcode = new ArrayList<String>();

    /**
     * 新加入一行代码
     * 
     * @param code
     */
    public static void addCodeatLast(String code) {
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

    /* _____________________________________________________ */

    /** 字符串计数器，用以防止字符串重复 */
    private static int strNum = 1;

    /**
     * 创建全局字符串
     * 
     * @param length
     * @param str
     * @return 字符串名
     */
    public static String CreatGloStr(int length, String str) {
        StringBuilder sb = new StringBuilder();
        String name = ".str." + strNum++;

        sb.append("@" + name + " = private unnamed_addr constant [");

        sb.append(length + " x i8] c\"");

        sb.append(str + "\"");

        addCodeatLast(sb.toString());
        return name;
    }

    public static Integer CreatCalExp(boolean leftisConst, Integer left, boolean rightisConst, Integer right,
            String op) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

        sb.append("%" + retRegNO + " = ");

        switch (op) {
            case "*":
                sb.append("mul ");
                break;
            case "/":
                sb.append("sdiv ");
                break;
            case "%":
                sb.append("srem ");
                break;
            case "+":
                sb.append("add ");
                break;
            case "-":
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

    public static Integer CreatCallFunc(FuncSymbol funcSymbol, ArrayList<Integer> params, ArrayList<Boolean> isConst) { // FIXME:
                                                                                                                        // 调用前需做类型转换
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
        for (int i = 0; i < params.size(); i++) {
            if (funcSymbol.paramTypes.get(i) == VarTypes.Int) {
                if (isConst.get(i)) {
                    sb.append("i32 " + params.get(i));
                } else {
                    sb.append("i32 %" + params.get(i));
                }
            } else {
                if (isConst.get(i)) {
                    sb.append("i8 " + params.get(i));
                } else {
                    sb.append("i8 %" + params.get(i));
                }
            }

            if (i != params.size() - 1) {
                sb.append(", ");
            } else {
                sb.append(")");
            }

            // TODO: 函数调用,需要记录在函数体中改变的全局变量，并修改对应的寄存器
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
    public static Integer CreatAllocCode(Integer size, Integer type) {
        StringBuilder sb = new StringBuilder();
        Integer retRegNO = utils.getRegNum();

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

        addCodeatLast(sb.toString());
        return retRegNO;
    }

    public static void CreatStoreCode(Integer regNO, Integer addrNO, Integer type) { // TODO: store
        StringBuilder sb = new StringBuilder();
    }

}
