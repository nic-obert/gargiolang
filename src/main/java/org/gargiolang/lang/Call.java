package org.gargiolang.lang;

import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;

public enum Call {

    OPEN, CLOSE;

    public static void evaluate(Interpreter interpreter) {

        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();

        line.get(currentTokenIndex).setPriority(0);

        int depth = 1;
        int i = currentTokenIndex + 1;
        for (Token token = line.get(i); true; token = line.get(i)) {

            if (token.getType().equals(Token.TokenType.CALL)) {
                if (token.getValue().equals(OPEN)) depth ++;
                else {
                    depth --;
                    if (depth == 0) {
                        token.setPriority(0);
                        return;
                    }
                }
            }

            if (token.getPriority() != 0) token.incrementPriority();
            i ++;
        }

    }

}
