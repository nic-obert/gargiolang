package org.gargiolang.runtime.function;

import org.gargiolang.parsing.tokens.Token;

import java.util.LinkedList;

public class Call {

    private final int calledFromLine;
    private final int calledFromIndex;
    private final int scopeCount;
    private final Function function;
    private final LinkedList<Token> lineState;

    public Call(int calledFromIndex, int calledFromLine, int scopeCount, Function function, LinkedList<Token> lineState) {
        this.calledFromIndex = calledFromIndex;
        this.calledFromLine = calledFromLine;
        this.scopeCount = scopeCount;
        this.function = function;
        this.lineState = lineState;
    }

    public Function getFunction() {
        return function;
    }

    public int getCalledFromIndex() {
        return calledFromIndex;
    }

    public int getCalledFromLine() {
        return calledFromLine;
    }

    public int getScopeCount() {
        return scopeCount;
    }

    public LinkedList<Token> getLineState() {
        return lineState;
    }
}
