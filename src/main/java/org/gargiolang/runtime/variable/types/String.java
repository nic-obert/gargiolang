package org.gargiolang.runtime.variable.types;

import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.lang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.lang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.runtime.Runtime;

public class String extends Type {

    public static Token add(java.lang.String a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException {
        Token result = new Token(Token.TokenType.STR, null);

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
        Token result = new Token(Token.TokenType.STR, null);

        switch (b.getVarType(Runtime.getRuntime()))
        {
            case INT -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int times = (int) b.getVarValue(Runtime.getRuntime()); times != 0; times--) {
                    stringBuilder.append(a);
                }
                result.setValue(stringBuilder.toString());
            }

            default -> throw new UnhandledOperationException("Unhandled operation: multiplication between String and " + b);
        }

        return result;
    }

}
