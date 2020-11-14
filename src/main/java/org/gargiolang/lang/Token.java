package org.gargiolang.lang;

public class Token {

    private final TokenType tokenType;
    private final Object value;
    private final int priority;

    public Token(TokenType tokenType, Object value, int priority){
        this.tokenType = tokenType;
        this.value = value;
        this.priority = priority;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public Object getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public enum TokenType {

        TXT, STR, NUM;

        public enum OperatorType {
            ADD, SUB, MUL, DIV, POW;
        }

    }
}
