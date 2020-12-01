package org.gargiolang.lang;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.lang.operators.ArithmeticOperator;
import org.gargiolang.lang.operators.LogicalOperator;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class Token {

    private final TokenType tokenType;
    private Object value;
    private int priority; // priority should not be final


    public Token(TokenType tokenType, Object value) {
        this.tokenType = tokenType;
        this.value = value;

        // switch statement for setting priority
        switch (tokenType)
        {
            case ARITHMETIC_OPERATOR -> this.priority = ((ArithmeticOperator) this.value).getPriority();

            case LOGICAL_OPERATOR -> this.priority = ((LogicalOperator) this.value).getPriority();

            case KEYWORD -> this.priority = ((Keyword) this.value).getPriority();

            default -> this.priority = tokenType.getPriority();
        }
    }


    public Token add(Token other) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
        // let the variable type handle the operation
        return this.getVarType(Runtime.getRuntime()).sum(this, other);
    }

    public Token subtract(Token other) throws UnrecognizedTypeException, UnhandledOperationException, UnimplementedException, UndeclaredVariableException {
        return this.getVarType(Runtime.getRuntime()).subtract(this, other);
    }

    public Token multiply(Token other) throws UnrecognizedTypeException, UnhandledOperationException, UnimplementedException, UndeclaredVariableException {
        return this.getVarType(Runtime.getRuntime()).multiply(this, other);
    }

    public Token divide(Token other) throws UnrecognizedTypeException, ZeroDivisionException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
        return this.getVarType(Runtime.getRuntime()).divide(this, other);
    }

    public Token mod(Token other) throws UnrecognizedTypeException, ZeroDivisionException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
        return this.getVarType(Runtime.getRuntime()).mod(this, other);
    }

    public Token power(Token other) throws UnrecognizedTypeException, UnimplementedException, UnhandledOperationException, UndeclaredVariableException {
        return this.getVarType(Runtime.getRuntime()).power(this, other);
    }

    public void increment() throws UnhandledOperationException, UndeclaredVariableException, UnrecognizedTypeException, UnimplementedException {
        // check if token is a variable
        if (!this.getType().equals(TokenType.TXT)) throw new UnhandledOperationException("Can only increment a variable, but " + this + " was provided");
        this.getVarType(Runtime.getRuntime()).increment(this);
    }

    public void decrement() throws UnhandledOperationException, UndeclaredVariableException, UnrecognizedTypeException, UnimplementedException {
        // check if token is a variable
        if (!this.getType().equals(TokenType.TXT)) throw new UnhandledOperationException("Can only decrement a variable, but " + this + " was provided");
        this.getVarType(Runtime.getRuntime()).decrement(this);
    }

    public Token greaterThan(Token other) throws UnrecognizedTypeException, UnimplementedException, UnhandledOperationException, UndeclaredVariableException{
        return this.getVarType(Runtime.getRuntime()).greaterThan(this, other);
    }

    public Token lessThan(Token other) throws UnrecognizedTypeException, UnimplementedException, UnhandledOperationException, UndeclaredVariableException{
        return this.getVarType(Runtime.getRuntime()).lessThan(this, other);
    }

    public Token equalsTo(Token other) throws UnrecognizedTypeException, UnimplementedException, UnhandledOperationException, UndeclaredVariableException{
        return this.getVarType(Runtime.getRuntime()).equalsTo(this, other);
    }

    public Token notEqualsTo(Token other) throws UnrecognizedTypeException, UnhandledOperationException, UnimplementedException, UndeclaredVariableException {
        return this.getVarType(Runtime.getRuntime()).notEqualsTo(this, other);
    }

    public Token not() throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
        return this.getVarType(Runtime.getRuntime()).not(this);
    }

    /**
     * Returns the boolean representation of the token's value
     *
     * @return the boolean representation of the token's value
     */
    public Token asBool() throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
        if (this.getType().equals(TokenType.BOOL)) return this;
        return this.getVarType(Runtime.getRuntime()).asBool(this);
    }

    public TokenType getType() {
        return tokenType;
    }

    // return the type of the token's value, not the token's
    public Variable.Type getVarType(Runtime runtime) throws UnrecognizedTypeException {
        if (this.getType().equals(TokenType.TXT)) {
            return runtime.getSymbolTable().getVariable((String) this.value).getType();
        }
        return Variable.Type.extractVarType(this);
    }


    public Object getValue() {
        return value;
    }

    // if token is a variable --> return its value, otherwise return token's value
    public Object getVarValue(Runtime runtime) throws UndeclaredVariableException {
        if (this.getType().equals(TokenType.TXT)) return runtime.getSymbolTable().getVariableThrow((String) this.value).getValue();
        else return getValue();
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void incrementPriority() {
        this.priority += 10;
    }


    public enum TokenType {

        TXT((byte) 0),
        STR((byte) 0),
        NUM((byte) 0),
        BOOL((byte) 0),
        NULL((byte) 0),

        TYPE((byte) 0),

        SCOPE((byte) 1),

        ASSIGNMENT_OPERATOR((byte) 1),

        FUNC((byte) 2),
        CALL((byte) 10),

        PAREN((byte) 10), // highest priority

        LOGICAL_OPERATOR((byte) 0), // priority depends on the operator
        ARITHMETIC_OPERATOR((byte) 0), // priority depends on the operator
        KEYWORD((byte) 0); // priority depends on the keyword



        private final byte priority;
        TokenType(byte i) {
            this.priority = i;
        }

        public int getPriority() {
            return priority;
        }


        /**
         * Converts a variable type to a token type
         *
         * @param type the variable type
         * @return the matching token type
         * @throws BadTypeException if the provided type has no conversion
         */
        public static TokenType fromVarType(Variable.Type type) throws BadTypeException {
            switch (type)
            {
                case STRING: return STR;
                case INT, FLOAT: return NUM;
                case BOOLEAN: return BOOL;
            }

            throw new BadTypeException("No conversion from variable type '" + type + "' to Token type");
        }

    }


    public void buildValue(char c) {
        // build the token's value based on it's type
        switch (tokenType) {
            case NUM, TXT, STR -> value += Character.toString(c);
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

    public static boolean isLogicalOperator(char c){
        return LogicalOperator.isLogicalOperator(c);
    }

    // returns the index of the token with the highest priority in a linked list of tokens
    public static int getHighestPriority(LinkedList<Token> line) {
        // TODO implement a linked list from scratch with builtin optimizations for token indexing
        int highestIndex = 0;
        int tokenIndex = 0;
        Token highestToken = line.getFirst();

        // if the first token of the line is a scope --> evaluate it right away
        if (highestToken.getType().equals(TokenType.SCOPE)) return 0;

        for (Token token : line) {
            if (token.getPriority() > highestToken.getPriority()) {
                highestToken = token;
                highestIndex = tokenIndex;

                // line evaluation should not go beyond the current scope
                if (token.getType().equals(TokenType.SCOPE)) break;
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
