package org.gargiolang.lang;

import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;

public enum Parenthesis {

    OPEN, CLOSED;

    public static void evaluate(Interpreter interpreter) {
        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();

        // number of opening parenthesis encountered
        int parenCount = 1;
        for (int counter = currentTokenIndex + 1; true; counter++) {
            Token token = line.get(counter);

            if (token.getType().equals(Token.TokenType.PAREN)) {
                if (token.getValue().equals(Parenthesis.OPEN)) {
                    parenCount ++;
                } else {
                    parenCount --;
                    // check if reached matching closing parenthesis
                    if (parenCount == 0) {
                        // remove closing parenthesis token
                        line.remove(counter);
                        break;
                    }
                }
            }
            if (token.getPriority() != 0) token.incrementPriority();
        }

        // remove opening parenthesis token
        line.remove(currentTokenIndex);
    }

}
