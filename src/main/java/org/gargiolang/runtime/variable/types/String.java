package org.gargiolang.runtime.variable.types;

import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.parsing.tokens.Token;
import org.gargiolang.parsing.tokens.TokenType;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

public class String extends Type {

    public static Token add(java.lang.String a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException {
        Token result = new Token(TokenType.STR, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> result.setValue(a + (int) b.getVarValue(Runtime.getRuntime()));

            case FLOAT -> result.setValue(a + (double) b.getVarValue(Runtime.getRuntime()));

            case STRING -> result.setValue(a + b.getVarValue(Runtime.getRuntime()));

            default -> throw new UnhandledOperationException("Unhandled operation: sum between String and " + b);
        }

        return result;
    }

    public static Token multiply(java.lang.String a, Token b) throws UnrecognizedTypeException, UnhandledOperationException, UndeclaredVariableException {
        Token result = new Token(TokenType.STR, null);

        if (b.getVarType(Runtime.getRuntime()) == Variable.Type.INT) {
            result.setValue(java.lang.String.valueOf(a).repeat(Math.max(0, (int) b.getVarValue(Runtime.getRuntime()))));
        } else {
            throw new UnhandledOperationException("Unhandled operation: multiplication between String and " + b);
        }

        return result;
    }

    private static int findNextChar(java.lang.String string, int index, char c){
        for(; index < string.length(); index++){
            if (string.charAt(index) == c) return index;
        }

        return -1;
    }

    public static Token equalsTo(java.lang.String a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException {
        Token result = new Token(TokenType.BOOL, null);

        if (b.getVarType(Runtime.getRuntime()) == Variable.Type.STRING) {
            result.setValue(a.equals(b.getVarValue(Runtime.getRuntime())));
        } else {
            throw new UnhandledOperationException("Unhandled operation: equality between String and " + b);
        }

        return result;
    }

    public static Token asBool(java.lang.String a) {
        return new Token(TokenType.BOOL, a.length() != 0);
    }

}
