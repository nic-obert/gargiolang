package org.gargiolang.runtime.variable.types;

import org.gargiolang.exception.evaluation.OpenScopeException;
import org.gargiolang.lang.Token;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

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

        if (b.getVarType(Runtime.getRuntime()) == Variable.Type.INT) {
            result.setValue(java.lang.String.valueOf(a).repeat(Math.max(0, (int) b.getVarValue(Runtime.getRuntime()))));
        } else {
            throw new UnhandledOperationException("Unhandled operation: multiplication between String and " + b);
        }

        return result;
    }

    //TODO not sure if this is the correct place to put this
    public static void evaluate(Interpreter interpreter) throws OpenScopeException, UndeclaredVariableException {
        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        Token token = line.get(currentTokenIndex);

        //TODO very shitty solution
        if(token.getValue() != null) {

            java.lang.String value = token.getValue().toString();

            if (value.contains("${")) {
                java.lang.String finalValue = value;
                for (int i = 0; i < finalValue.length(); i++) {
                    char c = finalValue.charAt(i);
                    if (i < finalValue.length() - 1) {
                        char next = finalValue.charAt(i + 1);

                        if (c == '$' && next == '{') {
                            int closing = findNextChar(finalValue, i, '}');

                            if (closing == -1) {
                                throw new OpenScopeException("No closing brackets");
                            }

                            java.lang.String str = finalValue.substring(i + 2, closing);
                            Variable var = interpreter.getRuntime().getSymbolTable().getVariableThrow(str);
                            Object val = var.getValue();

                            java.lang.String before = finalValue.substring(0, i);
                            java.lang.String after = finalValue.substring(closing + 1);

                            finalValue = before + val + after;
                        }
                    }
                }
                token.setValue(finalValue);
            }
        }

        token.setPriority(0);

    }

    private static int findNextChar(java.lang.String string, int index, char c){
        for(; index < string.length(); index++){
            if (string.charAt(index) == c) return index;
        }

        return -1;
    }

    public static Token equalsTo(java.lang.String a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException {
        Token result = new Token(Token.TokenType.BOOL, null);

        if (b.getVarType(Runtime.getRuntime()) == Variable.Type.STRING) {
            result.setValue(a.equals(b.getVarValue(Runtime.getRuntime())));
        } else {
            throw new UnhandledOperationException("Unhandled operation: equality between String and " + b);
        }

        return result;
    }

    public static Token asBool(java.lang.String a) {
        return new Token(Token.TokenType.BOOL, a.length() != 0);
    }

}
