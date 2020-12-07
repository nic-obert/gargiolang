package org.gargiolang.tokenizer.tokens;

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
        currentToken.setPriority(-1);

        int depth = 1;

        for (Token token = currentToken.getNext(); true; token = token.getNext()) {

            if (token.getType() == TokenType.CALL) {
                if (token.getValue() == OPEN)
                    depth ++;
                else {
                    depth --;
                    if (depth == 0) {
                        token.setPriority(-1);
                        return;
                    }
                }
            }

            if (token.getPriority() > 1) // greater or equal to 0, but faster execution
                token.incrementPriority();
        }

    }

    public int getPriority() {
        if (this == OPEN)
            return 10;
        return 0; // closing function call is just a placeholder and it shall not be evaluated
    }
}
