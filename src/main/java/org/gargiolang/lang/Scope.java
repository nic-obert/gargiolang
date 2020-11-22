package org.gargiolang.lang;

import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;

public enum Scope {

    OPEN, CLOSE;

    public static void evaluate(Interpreter interpreter) {
        LinkedList<Token> line = interpreter.getLine();
        // remove token from line since it's been evaluated
        Token scope = line.remove(interpreter.getCurrentTokenIndex());

        // add or pop scope based on whether the token is { or }
        if (scope.getValue().equals(OPEN)) interpreter.getRuntime().getSymbolTable().addScope();
        else interpreter.getRuntime().getSymbolTable().popScope();
    }

}
