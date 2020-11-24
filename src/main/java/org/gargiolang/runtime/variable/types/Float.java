package org.gargiolang.runtime.variable.types;

import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.lang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.lang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.lang.exception.evaluation.ZeroDivisionException;
import org.gargiolang.runtime.Runtime;

public class Float extends Type {
    public static Token add(double a, Token b) throws UnhandledOperationException, UndeclaredVariableException, UnrecognizedTypeException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case INT -> result.setValue(a + (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a + (double) b.getVarValue(Runtime.getRuntime()));


            default -> throw new UnhandledOperationException("Unhandled operation: sum between Float and " + b);
        }

        return result;
    }

    public static Token subtract(double a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case FLOAT -> result.setValue(a - (double) b.getVarValue(Runtime.getRuntime()));

            case INT -> result.setValue(a - (int) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: subtraction between Float and " + b);
        }

        return result;
    }

    public static Token multiply(double a, Token b) throws UnhandledOperationException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case INT -> result.setValue(a * (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a * (double) b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: multiplication between Float and " + b);
        }

        return result;
    }

    public static Token divide(double a, Token b) throws UnhandledOperationException, ZeroDivisionException, UnrecognizedTypeException, UndeclaredVariableException {
        Token result = new Token(Token.TokenType.NUM, null);

        switch (b.getVarType(Runtime.getRuntime())) {
            case FLOAT -> {
                double bValue = (double) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a / bValue);
            }

            case INT -> {
                int bValue = (int) b.getVarValue(Runtime.getRuntime());
                if (bValue == 0) throw new ZeroDivisionException("Cannot divide by zero");
                result.setValue(a / bValue);
            }

            default -> throw new UnhandledOperationException("Unhandled operation: division between Float and " + b);
        }
        return result;
    }
}
