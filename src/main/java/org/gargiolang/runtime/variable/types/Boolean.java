package org.gargiolang.runtime.variable.types;

import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.lang.exception.evaluation.UnhandledOperationException;
import org.gargiolang.lang.exception.evaluation.UnimplementedException;
import org.gargiolang.lang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.runtime.Runtime;

public class Boolean extends Type {

    public static Token equalsTo(boolean a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
        Token result = new Token(Token.TokenType.BOOL, null);

        switch (b.getVarType(Runtime.getRuntime())) {

            case BOOLEAN -> result.setValue(a == (boolean) b.getVarValue(Runtime.getRuntime()));

            default -> result.setValue(a == (boolean) b.asBool().getValue());
        }
        return result;
    }

}
