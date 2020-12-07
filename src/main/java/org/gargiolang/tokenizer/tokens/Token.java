package org.gargiolang.tokenizer.tokens;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.tokenizer.tokens.operators.ArithmeticOperator;
import org.gargiolang.tokenizer.tokens.operators.LogicalOperator;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

public class Token {

    private static int classId;

    private final TokenType tokenType;
    private Object value;
    private int priority; // priority should not be final
    private final int id;

    // for TokenLine doubly-linked list
    private Token prev;
    private Token next;


    public Token(TokenType tokenType, Object value) {
        this.id = Token.getId();
        this.tokenType = tokenType;
        this.value = value;

        // switch statement for setting priority
        switch (tokenType)
        {
            case ARITHMETIC_OPERATOR -> this.priority = ((ArithmeticOperator) this.value).getPriority();

            case LOGICAL_OPERATOR -> this.priority = ((LogicalOperator) this.value).getPriority();

            case KEYWORD -> this.priority = ((Keyword) this.value).getPriority();

            case CALL -> this.priority = ((Call) this.value).getPriority();

            default -> this.priority = tokenType.getPriority();
        }
    }

    /**
     * To be used when copying a token
     */
    private Token(TokenType tokenType, Object value, int priority, int id) {
        this.tokenType = tokenType;
        this.value = value;
        this.priority = priority;
        this.id = id;
    }


    public static int getId() {
        return classId ++;
    }


    public void buildValue(char c) {
        // build the token's value based on it's type
        switch (tokenType) {
            case NUM, TXT, STR -> value += Character.toString(c);
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
        if (this.getType() != TokenType.TXT)
            throw new UnhandledOperationException("Can only increment a variable, but " + this + " was provided");
        this.getVarType(Runtime.getRuntime()).increment(this);
    }

    public void decrement() throws UnhandledOperationException, UndeclaredVariableException, UnrecognizedTypeException, UnimplementedException {
        // check if token is a variable
        if (this.getType() != TokenType.TXT)
            throw new UnhandledOperationException("Can only decrement a variable, but " + this + " was provided");
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
        if (this.getType() == TokenType.BOOL)
            return this;
        return this.getVarType(Runtime.getRuntime()).asBool(this);
    }


    public TokenType getType() {
        return tokenType;
    }

    // return the type of the token's value, not the token's
    public Variable.Type getVarType(Runtime runtime) throws UnrecognizedTypeException, UndeclaredVariableException {
        if (this.getType() == TokenType.TXT) {
            return runtime.getSymbolTable().getVariableThrow((String) this.value).getType();
        }
        return Variable.Type.extractVarType(this);
    }


    public Object getValue() {
        return value;
    }

    // if token is a variable --> return its value, otherwise return token's value
    public Object getVarValue(Runtime runtime) throws UndeclaredVariableException {
        if (this.getType() == TokenType.TXT)
            return runtime.getSymbolTable().getVariableThrow((String) this.value).getValue();
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


    public Token getPrev() {
        return prev;
    }

    public Token getNext() {
        return next;
    }

    public void setNext(Token next) {
        this.next = next;
    }

    public void setPrev(Token prev) {
        this.prev = prev;
    }

    public boolean hasPrev() {
        return prev != null;
    }

    public boolean hasNext() {
        return next != null;
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


    public Token copy() {
        return new Token(tokenType, value, priority, id);
    }


    /**
     * To be used when comparing tokens that could possibly not occupy the same memory address (being the same token, but having different memory addresses)
     *
     * @param other other token to compare
     * @return whether the tokens are the same or not
     */
    public boolean is(Token other) {
        // TODO: 05/12/20 implement a better way to check if tokens are the same (when copying the line two copies of the same token are made, but with different memory address, thus not being exactly the same object)
        if (other == null)
            return false;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "<" + getType() + ": " + getValue() + " (" + getPriority() + ")>";
    }
}
