package org.gargiolang.lang;

import java.util.LinkedList;

public class Token {

    private final TokenType tokenType;
    private Object value;
    private int priority; // priority should not be final


    public Token(TokenType tokenType, Object value) {
        this.tokenType = tokenType;
        this.value = value;

        switch (tokenType) {
            case ARITHMETIC_OPERATOR:
                this.priority = ((ArithmeticOperator) this.value).getPriority();
                break;

            default:
                this.priority = tokenType.getPriority();
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

        TXT((byte) 0),
        STR((byte) 0),
        NUM((byte) 0),
        BOOL((byte) 0),

        KEYWORD((byte) 1),
        ASSIGNMENT_OPERATOR((byte) 1),

        ARITHMETIC_OPERATOR((byte) 0), // priority depends on the operator

        PAREN((byte) 10),
        CALL((byte) 10);


        private final byte priority;
        TokenType(byte i) {
            this.priority = i;
        }

        public int getPriority() {
            return priority;
        }
    }


    public void buildValue(char c) {
        // build the token's value based on it's type
        switch (tokenType) {

            case NUM:

            case TXT:

            case STR:
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


    // returns the index of the token with the highest priority in a linked list of tokens
    public static int getHighestPriority(LinkedList<Token> line) {
        // TODO implement a linked list from scratch with builtin optimizations for token indexing
        int highestIndex = 0;
        int tokenIndex = 0;
        Token highestToken = line.getFirst();

        for (Token token : line) {
            if (token.getPriority() > highestToken.getPriority()) {
                highestToken = token;
                highestIndex = tokenIndex;
            }
            tokenIndex ++;
        }

        return highestIndex;
    }


    @Override
    public String toString() {
        return "<" + getType() + ": " + getValue() + " (" + getPriority() + ")>";
    }
}
