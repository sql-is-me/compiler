package frontend.Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import frontend.ErrorLog;

public class Lexer {
    private static int count = 0;

    public static class Token { // token类
        public String str;
        public String tk;
        public int line;
        public int id;
        public boolean status;

        Token(String str, String tk, int line, boolean status) {
            if (status) {
                this.id = count++;
            } else {
                this.id = -1;
            }
            this.str = str;
            this.tk = tk;
            this.line = line;
            this.status = status;
        }

        @Override
        public String toString() {
            return tk + " " + str + "\n";
        }
    }

    private static final HashMap<String, String> SAVEWORDS; // 保留字
    static { // 保留字表
        SAVEWORDS = new HashMap<>();
        SAVEWORDS.put("main", "MAINTK");
        SAVEWORDS.put("const", "CONSTTK");
        SAVEWORDS.put("int", "INTTK");
        SAVEWORDS.put("char", "CHARTK");
        SAVEWORDS.put("break", "BREAKTK");
        SAVEWORDS.put("continue", "CONTINUETK");
        SAVEWORDS.put("if", "IFTK");
        SAVEWORDS.put("else", "ELSETK");
        SAVEWORDS.put("!", "NOT");
        SAVEWORDS.put("&&", "AND");
        SAVEWORDS.put("||", "OR");
        SAVEWORDS.put("for", "FORTK");
        SAVEWORDS.put("getint", "GETINTTK");
        SAVEWORDS.put("getchar", "GETCHARTK");
        SAVEWORDS.put("printf", "PRINTFTK");
        SAVEWORDS.put("return", "RETURNTK");
        SAVEWORDS.put("+", "PLUS");
        SAVEWORDS.put("-", "MINU");
        SAVEWORDS.put("void", "VOIDTK");
        SAVEWORDS.put(";", "SEMICN");
        SAVEWORDS.put("*", "MULT");
        SAVEWORDS.put("/", "DIV");
        SAVEWORDS.put("%", "MOD");
        SAVEWORDS.put("<", "LSS");
        SAVEWORDS.put("<=", "LEQ");
        SAVEWORDS.put(">", "GRE");
        SAVEWORDS.put(">=", "GEQ");
        SAVEWORDS.put("==", "EQL");
        SAVEWORDS.put("!=", "NEQ");
        SAVEWORDS.put("=", "ASSIGN");
        SAVEWORDS.put(",", "COMMA");
        SAVEWORDS.put("(", "LPARENT");
        SAVEWORDS.put(")", "RPARENT");
        SAVEWORDS.put("[", "LBRACK");
        SAVEWORDS.put("]", "RBRACK");
        SAVEWORDS.put("{", "LBRACE");
        SAVEWORDS.put("}", "RBRACE");
    }

    private static int lineNum = 0; // 行数
    private static int pos; // 位置
    private static int length; // 行长
    private static String line; // 行

    // enum Status {
    // normal, // 正常状态
    // identify, // 识别符状态
    // annotation, // 注释状态
    // chartk, // 字符状态
    // string // 字符串状态
    // }

    // private static Status status = Status.normal;

    public static List<Token> tokens = new ArrayList<>();

    private static void NumberToken() {
        StringBuilder number = new StringBuilder();
        while (pos < length && Character.isDigit(line.charAt(pos))) {
            number.append(line.charAt(pos));
            pos++;
        }

        tokens.add(new Token(number.toString(), "INTCON", lineNum, true));
    }

    private static void CharToken() {
        StringBuilder temp = new StringBuilder();
        temp.append(line.charAt(pos));
        pos++;

        while (pos < length && line.charAt(pos) != '\'') {
            if (line.charAt(pos) == '\\') {
                temp.append(line.charAt(pos));
                temp.append(line.charAt(pos + 1));
                pos += 2;
            } else {
                temp.append(line.charAt(pos));
                pos++;
            }
        }

        if (line.charAt(pos) == '\'') {
            temp.append(line.charAt(pos));
            pos++;
        }
        tokens.add(new Token(temp.toString(), "CHRCON", lineNum, true));
    }

    private static void StringToken() {
        StringBuilder temp = new StringBuilder();
        temp.append(line.charAt(pos));
        pos++;

        while (pos < length && line.charAt(pos) != '\"') {
            temp.append(line.charAt(pos));
            pos++;
        }

        if (line.charAt(pos) == '\"') {
            temp.append(line.charAt(pos));
            pos++;
        }
        tokens.add(new Token(temp.toString(), "STRCON", lineNum, true));
    }

    private static void LetterToken() {
        StringBuilder temp = new StringBuilder();
        while (pos < length && (Character.isLetterOrDigit(line.charAt(pos)) || line.charAt(pos) == '_')) {
            temp.append(line.charAt(pos));
            pos++;
        }

        String word = temp.toString();
        if (SAVEWORDS.containsKey(word)) {
            tokens.add(new Token(word, SAVEWORDS.get(word), lineNum, true));
        } else {
            tokens.add(new Token(word, "IDENFR", lineNum, true));
        }
    }

    private static void OperatorToken1() {
        StringBuilder temp = new StringBuilder();
        temp.append(line.charAt(pos));
        String operator = temp.toString();
        pos++;
        tokens.add(new Token(operator, SAVEWORDS.get(operator), lineNum, true));
    }

    private static void OperatorToken2() {
        char cc = line.charAt(pos);
        StringBuilder temp = new StringBuilder();
        temp.append(cc);
        pos++;
        if (pos < length) {
            if (cc == '!' && line.charAt(pos) == '=') {
                temp.append(line.charAt(pos));
                pos++;
            } else if (cc == '=' && line.charAt(pos) == '=') {
                temp.append(line.charAt(pos));
                pos++;
            } else if (cc == '<' && line.charAt(pos) == '=') {
                temp.append(line.charAt(pos));
                pos++;
            } else if (cc == '>' && line.charAt(pos) == '=') {
                temp.append(line.charAt(pos));
                pos++;
            }
        }
        String operator = temp.toString();

        tokens.add(new Token(operator, SAVEWORDS.get(operator), lineNum, true));
    }

    private static void JumpAnnotation(BufferedReader br) throws IOException {
        pos += 2;
        while (pos < length) {
            if (line.charAt(pos) != '*') {
                pos++;
            } else {
                if (pos < length - 1 && line.charAt(pos + 1) == '/') {
                    pos += 2;
                    return;
                } else {
                    pos++;
                }
            }
        }

        while ((line = br.readLine()) != null) {
            lineNum++;
            length = line.length();
            pos = 0;

            while (pos < length) {
                if (line.charAt(pos) != '*') {
                    pos++;
                } else {
                    if (pos < length - 1 && line.charAt(pos + 1) == '/') {
                        pos += 2;
                        return;
                    } else {
                        pos++;
                    }
                }
            }
        }
    }

    // 词法分析器
    public void lexer(BufferedReader br) throws IOException {
        char cc;
        while ((line = br.readLine()) != null) {
            lineNum++;
            length = line.length();
            pos = 0;
            while (pos < length) {
                cc = line.charAt(pos);

                if (Character.isWhitespace(cc)) { // 空格快速跳过
                    pos++;
                    continue;
                } else if (cc == '/') {
                    if (pos < length - 1 && line.charAt(pos + 1) == '/') {
                        break;
                    } else if (pos < length - 1 && line.charAt(pos + 1) == '*') {
                        JumpAnnotation(br);
                    } else {
                        OperatorToken1();
                    }
                } else if (Character.isDigit(cc)) { // 数字常量
                    NumberToken();
                } else if (cc == '\'') { // 字符常量
                    CharToken();
                } else if (cc == '\"') {// 字符串常量
                    StringToken();
                } else if (Character.isLetter(cc) || cc == '_') { // 字母
                    LetterToken();
                } else if (cc == '+' || cc == '-' || cc == '*' || cc == '/' || cc == '%' || cc == ';' || cc == ','
                        || cc == '[' || cc == ']' || cc == '{' || cc == '}' || cc == '(' || cc == ')') { // 运算符
                    OperatorToken1();
                } else if (cc == '!' || cc == '<' || cc == '>' || cc == '=') { // 需要特判的运算符
                    OperatorToken2();
                } else if (cc == '&' || cc == '|') { // 需要特判的运算符2
                    StringBuilder temp = new StringBuilder();
                    temp.append(cc);

                    if (pos == length - 1 || (pos < length - 1 &&
                            ((cc == '&' && line.charAt(pos + 1) != '&')
                                    || (cc == '|' && line.charAt(pos + 1) != '|')))) {
                        ErrorLog.makelog_error(lineNum, 'a');
                        if (cc == '&') {
                            tokens.add(new Token("&&", SAVEWORDS.get("&&"), lineNum, false));
                        } else if (cc == '|') {
                            tokens.add(new Token("||", SAVEWORDS.get("||"), lineNum, false));
                        }

                        // String operator = temp.toString();
                        // tokens.add(new Token(operator, "WRONG"));
                    } else {
                        temp.append(line.charAt(pos + 1));
                        String operator = temp.toString();
                        tokens.add(new Token(operator, SAVEWORDS.get(operator), lineNum, true));

                        pos++; // fix bug, if none error jump '&'
                    }
                    pos++;
                } else {
                    pos++;
                }
            }
        }
        WriteLexerAns.WriteAnswer(tokens);
    }
}