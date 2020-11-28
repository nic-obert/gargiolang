package org.gargiolang.lang.operators;

import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.exception.evaluation.OpenParenthesisException;
import org.gargiolang.lang.Token;
import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;

public enum Parenthesis {

    OPEN, CLOSED;


    public static int[][] findNextParenthesis(Interpreter interpreter) throws IndexOutOfBoundsException, OpenParenthesisException {
        int[][] parenthesis = new int[2][];
        int lineIndex = interpreter.getLineIndex();

        LinkedList<Token> line = interpreter.getLine();
        for (int parenCount = 0; true; line = interpreter.getLine(lineIndex)) {

            for (Token token : line) {
                if (token.getType().equals(Token.TokenType.PAREN)) {

                    if (token.getValue().equals(Parenthesis.OPEN)) {
                        if (parenCount == 0) {
                            parenthesis[0] = new int[]{lineIndex, interpreter.getLine(lineIndex).indexOf(token)};
                        }
                        parenCount ++;
                    } else {
                        parenCount --;
                        if (parenCount == 0) {
                            parenthesis[1] = new int[]{lineIndex, interpreter.getLine(lineIndex).indexOf(token)};
                            return parenthesis;
                        }
                    }
                }
            }

            lineIndex ++;
            if (lineIndex == interpreter.getTokens().size()) throw new OpenParenthesisException("Parenthesis is open, but never closed");
        }
    }


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
