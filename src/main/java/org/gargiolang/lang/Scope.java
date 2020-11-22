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

    /**
     *
     * @param interpreter the interpreter that is currently executing the script
     * @return position of the next matching scopes [[line, pos], [line, pos]]
     */
    public static int[][] findNextScope(Interpreter interpreter) {

        int[][] scopes = new int[2][2];
        int lineIndex = interpreter.getLineIndex();

        for (int scopeCount = 0; scopeCount != -1; lineIndex++) {
            LinkedList<Token> line = interpreter.getTokens().get(lineIndex);

            for (Token token : line) {
                if (token.getType().equals(Token.TokenType.SCOPE)) {

                    // if the scope is an open scope
                    if (token.getValue().equals(OPEN)) {
                        if (scopeCount == 0) {
                            scopes[0] = new int[]{lineIndex, line.indexOf(token)};
                        }
                        scopeCount ++;
                    } else {
                        // if the scope is a closed scope
                        scopeCount --;
                        if (scopeCount == 0) {
                            // means that the matching closing scope has been found
                            scopes[1] = new int[]{lineIndex, line.indexOf(token)};
                            scopeCount = -1; // this breaks the outer loop
                            break;
                        }
                    }
                }
            }
        }

        return scopes;
    }

}
