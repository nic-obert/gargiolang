package org.gargiolang.parsing.tokens;

import org.gargiolang.runtime.Interpreter;

public enum Call {

    OPEN, CLOSE;

    /**
     * Increases the priority of tokens between Call tokens
     *
     * @param interpreter the interpreter
     */
    public static void evaluate(Interpreter interpreter) {

        Token currentToken =  interpreter.getCurrentToken();
        currentToken.setPriority(0);

        int depth = 1;

        for (Token token = currentToken.getNext(); true; token = token.getNext()) {

            if (token.getType() == TokenType.CALL) {
                if (token.getValue() == OPEN)
                    depth ++;
                else {
                    depth --;
                    if (depth == 0) {
                        token.setPriority(0);
                        return;
                    }
                }
            }

            if (token.getPriority() != 0)
                token.incrementPriority();
        }

    }

}
