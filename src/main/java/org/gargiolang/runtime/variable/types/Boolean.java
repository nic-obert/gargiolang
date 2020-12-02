package org.gargiolang.runtime.variable.types;

import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnimplementedException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.parsing.tokens.Token;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

public class Boolean extends Type {

    public static Token equalsTo(boolean a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnimplementedException {
        Token result = new Token(Token.TokenType.BOOL, null);

        if (b.getVarType(Runtime.getRuntime()) == Variable.Type.BOOLEAN) {
            result.setValue(a == (boolean) b.getVarValue(Runtime.getRuntime()));
        } else {
            result.setValue(a == (boolean) b.asBool().getValue());
        }
        return result;
    }

}
