package org.gargiolang.lang;

import java.util.Arrays;

public class Token {

    private final TokenType tokenType;
    private Object value;
    private int priority; // priority should not be final


    public Token(TokenType tokenType, Object value) {
        this.tokenType = tokenType;
        this.value = value;

        switch (tokenType) {
            case ARITHMETIC_OPERATOR:
                this.priority = ArithmeticOperator.getPriority(value.toString());
                break;

            default:
                this.priority = tokenType.priority;
                break;
        }
    }

    public TokenType getType() {
        return tokenType;
    }

    public Object getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public enum TokenType {

        TXT(0),
        STR(0),
        NUM(0),

        KEYWORD(1),

        ARITHMETIC_OPERATOR(0), // priority depends on the operator

        PAREN(10);



        public int priority;
        TokenType(int i) {
            this.priority = i;
        }

    }


    public void buildValue(char c) {
        // build the token's value based on it's type
        switch (tokenType) {

            case NUM:
                value += Character.toString(c);
                break;

            case TXT:
                value += Character.toString(c);
                break;
        }
    }


    public static boolean isText(char c) {
        // see the ASCII table
        return (64 < c && c < 91) || (c == '_') || (96 < c && c < 123);
    }

    public static boolean isNumber(char c) {
        // see the ASCII table
        return (47 < c && c < 58);
    }

    public static boolean isArithmeticOperator(char c) {
        return ArithmeticOperator.isArithmeticOperator(c);
    }


    @Override
    public String toString() {
        return "<" + getType() + ": " + getValue() + " (" + getPriority() + ")>";
    }
}
