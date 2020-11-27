package org.gargiolang.runtime.variable.types;

import org.gargiolang.lang.Token;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.evaluation.ZeroDivisionException;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

public class Integer extends Type {
    public static Token add(int a, Token b) throws UnhandledOperationException, UndeclaredVariableException, UnrecognizedTypeException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> result.setValue(a + (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a + (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: sum between Integer and " + b);
        }

        return result;
    }

    public static Token subtract(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> result.setValue(a - (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a - (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: subtraction between Integer and " + b);
        }
        return result;
    }

    public static Token multiply(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> result.setValue(a * (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a * (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: multiplication between Integer and " + b);
        }
        return result;
    }

    public static Token divide(int a, Token b) throws UnhandledOperationException, ZeroDivisionException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> {
                int bValue = (int) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a / bValue);
            }

            case FLOAT -> {
                double bValue = (double) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a / bValue);
            }

            default -> throw new UnhandledOperationException("Unhandled operation: division between Integer and " + b);
        }
        return result;
    }

    public static Token mod(int a, Token b) throws UnhandledOperationException, ZeroDivisionException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> {
                int bValue = (int) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a % bValue);
            }

            case FLOAT -> {
                double bValue = (double) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a % bValue);
            }

            default -> throw new UnhandledOperationException("Unhandled operation: division between Integer and " + b);
        }
        return result;
    }

    public static Token power(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> result.setValue(Math.round(Math.pow(a, (int) b.getVarValue(Runtime.getRuntime()))));

            case FLOAT -> result.setValue(Math.pow(a, (double) b.getVarValue(Runtime.getRuntime())));

            default -> throw new UnhandledOperationException("Unhandled operation: power between Integer and " + b);
        }
        return result;
    }

    public static void increment(Token a) throws UndeclaredVariableException {
        Variable variable = Runtime.getRuntime().getSymbolTable().getVariableThrow((java.lang.String) a.getValue());
        variable.setValue((int) variable.getValue() + 1);
    }

    public static void decrement(Token a) throws UndeclaredVariableException {
        Variable variable = Runtime.getRuntime().getSymbolTable().getVariableThrow((java.lang.String) a.getValue());
        variable.setValue((int) variable.getValue() - 1);
    }

    public static Token greaterThan(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.BOOL, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case INT -> result.setValue(a > (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a > (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: greaterThan between Integer and " + b);
        }
        return result;
    }

    public static Token lessThan(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.BOOL, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case INT -> result.setValue(a < (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a < (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: lessThan between Integer and " + b);
        }
        return result;
    }

    public static Token equalsTo(int a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.BOOL, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case INT -> result.setValue(a == (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a == (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: equality between Integer and " + b);
        }
        return result;
    }

    public static Token asBool(int a) {
        return new Token(Token.TokenType.BOOL, a != 0);
    }
}
