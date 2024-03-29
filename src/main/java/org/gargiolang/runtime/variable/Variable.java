package org.gargiolang.runtime.variable;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenType;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.types.Boolean;
import org.gargiolang.runtime.variable.types.Float;
import org.gargiolang.runtime.variable.types.Integer;
import org.gargiolang.runtime.variable.types.String;

public class Variable {

    private Object value;
    private final Variable.Type type;
    private Accessibility accessibility;


    public Variable(Object value, Variable.Type type, Accessibility accessibility) {
        this.value = value;
        this.type = type;
        this.accessibility = accessibility;
    }


    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Accessibility getAccessibility() {
        return this.accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }


    public java.lang.String toString() {
        return "{ " + type + ", " + value + ", " + accessibility + " }";
    }


    public enum Type {

        INT,
        FLOAT,
        STRING,
        BOOLEAN,
        NULL;

        public static Type getType(java.lang.String str) {
            str = str.toUpperCase();
            for(Type type : Type.values()) {
                if (type.toString().equals(str))
                    return type;
            }
            return null;
        }

        // extract Variable.Type from Token
        public static Type extractVarType(Token token) throws UnrecognizedTypeException {
            switch (token.getType())
            {
                case NUM:
                    if (token.getValue() instanceof java.lang.Integer) return INT;
                    else return FLOAT;

                case STR:
                    return STRING;

                case BOOL:
                    return BOOLEAN;

                default:
                    throw new UnrecognizedTypeException("Cannot extract variable type from token: " + token);
            }
        }


        public Token sum(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            // TODO implement this using method overloading, reflection or this kind of stuff. I don't know if it's doable in java, but in C++ you can.

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.add((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.add((double) a.getVarValue(runtime), b);

                case STRING -> result = String.add((java.lang.String) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token subtract(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.subtract((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.subtract((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token multiply(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.multiply((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.multiply((double) a.getVarValue(runtime), b);

                case STRING -> result = String.multiply((java.lang.String) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token divide(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException, ZeroDivisionException {

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.divide((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.divide((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public Token mod(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException, ZeroDivisionException {

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.mod((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.mod((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public Token power(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.power((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.power((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public void increment(Token a) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
            switch (this)
            {
                case INT -> Integer.increment(a);

                case FLOAT -> Float.increment(a);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(Runtime.getRuntime()));
            }
        }

        public void decrement(Token a) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
            switch (this)
            {
                case INT -> Integer.decrement(a);

                case FLOAT -> Float.decrement(a);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(Runtime.getRuntime()));
            }
        }

        public Token greaterThan(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.greaterThan((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.greaterThan((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public Token lessThan(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.lessThan((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.lessThan((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public Token equalsTo(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException, UnhandledOperationException {
            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (this)
            {
                case INT -> result = Integer.equalsTo((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.equalsTo((double) a.getVarValue(runtime), b);

                case STRING -> result = String.equalsTo((java.lang.String) a.getVarValue(runtime), b);

                case BOOLEAN -> result = Boolean.equalsTo((boolean) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }

        public static Token greaterOrEquals(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            return new Token(TokenType.BOOL, (boolean) a.greaterThan(b).getValue() || (boolean) a.equalsTo(b).getValue());
        }

        public static Token lessOrEquals(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            return new Token(TokenType.BOOL, (boolean) a.lessThan(b).getValue() || (boolean) a.equalsTo(b).getValue());
        }

        public Token notEqualsTo(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            return equalsTo(a, b).not();
        }

        public Token asBool(Token a) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
            switch (this)
            {
                case INT -> {
                    return Integer.asBool((int) a.getVarValue(Runtime.getRuntime()));
                }

                case FLOAT -> {
                    return Float.asBool((double) a.getVarValue(Runtime.getRuntime()));
                }

                case STRING -> {
                    return String.asBool((java.lang.String) a.getVarValue(Runtime.getRuntime()));
                }

                case BOOLEAN -> {
                    return a;
                }

                default -> throw new UnimplementedException("Cannot convert type " + a.getVarType(Runtime.getRuntime()) + " to a boolean");
            }
        }

        public Token not(Token a) throws UnrecognizedTypeException, UnimplementedException, UndeclaredVariableException {
            Token bool = asBool(a);
            bool.setValue(!(boolean)bool.getValue());
            return bool;
        }

        public static Token or(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
            return new Token(TokenType.BOOL, (boolean) a.asBool().getValue() || (boolean) b.asBool().getValue());
        }

        public static Token and(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
            return new Token(TokenType.BOOL, (boolean) a.asBool().getValue() && (boolean) b.asBool().getValue());
        }

    }
}
