package Frontend.Syntax.Children;

import java.util.ArrayList;

import Frontend.Lexer.Lexer.Token;

public class VarsAttribute {
    String name;
    Boolean isArray;
    int arrSize;
    ArrayList<Integer> initValues;
    ArrayList<ArrayList<Token>> valueExp;
    boolean zeroinitializer = false;

    public VarsAttribute(String name) {
        this.name = name;
        this.isArray = false;
        this.arrSize = 0;
        this.initValues = null;
        this.valueExp = null;
    }
}
