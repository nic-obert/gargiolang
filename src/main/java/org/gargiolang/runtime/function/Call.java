package org.gargiolang.runtime.function;

import org.gargiolang.parsing.tokens.Token;
import org.gargiolang.parsing.tokens.TokenLine;

public class Call {

    private final int calledFromLine;
    private final Token calledFromToken;
    private final int scopeCount;
    private final Function function;
    private final TokenLine lineState;

    public Call(Token calledFromToken, int calledFromLine, int scopeCount, Function function, TokenLine lineState) {
        this.calledFromToken = calledFromToken;
        this.calledFromLine = calledFromLine;
        this.scopeCount = scopeCount;
        this.function = function;
        this.lineState = lineState;
    }

    public Function getFunction() {
        return function;
    }

    public Token getCalledFromToken() {
        return calledFromToken;
    }

    public int getCalledFromLine() {
        return calledFromLine;
    }

    public int getScopeCount() {
        return scopeCount;
    }

    public TokenLine getLineState() {
        return lineState;
    }
}
