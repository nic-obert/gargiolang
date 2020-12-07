package org.gargiolang.tokenizer.tokens.operators;

import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.exception.evaluation.OpenParenthesisException;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenBlock;
import org.gargiolang.tokenizer.tokens.TokenLine;
import org.gargiolang.tokenizer.tokens.TokenType;
import org.gargiolang.runtime.Interpreter;

public enum Parenthesis {

    OPEN, CLOSED;


    public static TokenBlock findNextParenthesis(Interpreter interpreter) throws IndexOutOfBoundsException, OpenParenthesisException {

        int firstLine = 0;
        Token firstToken = null;

        int lineIndex = interpreter.getLineIndex();

        TokenLine line = interpreter.getLine();
        for (int parenCount = 0; true; line = interpreter.getLine(lineIndex)) {

            Token lastToken = line.getLast().getNext();
            for (Token token = line.getFirst(); token != lastToken; token = token.getNext()) {
                if (token.getType().equals(TokenType.PAREN)) {

                    if (token.getValue().equals(Parenthesis.OPEN)) {
                        if (parenCount == 0) {
                            firstLine = lineIndex;
                            firstToken = token;
                        }
                        parenCount ++;
                    } else {
                        parenCount --;
                        if (parenCount == 0) {
                            return new TokenBlock(firstLine, firstToken, lineIndex, token);
                        }
                    }
                }
            }

            lineIndex ++;
            if (lineIndex == interpreter.getTokens().size())
                throw new OpenParenthesisException("Parenthesis is open, but never closed");
        }
    }


    public static void evaluate(Interpreter interpreter) {
        TokenLine line = interpreter.getLine();
        Token token = interpreter.getCurrentToken();

        // remove opening parenthesis token
        line.remove(token);

        // number of opening parenthesis encountered
        int parenCount = 1;
        for (token = token.getNext(); true; token = token.getNext()) {

            if (token.getType() == TokenType.PAREN) {
                if (token.getValue() == Parenthesis.OPEN) {
                    parenCount ++;
                } else {
                    parenCount --;
                    // check if reached matching closing parenthesis
                    if (parenCount == 0) {
                        // remove closing parenthesis token
                        line.remove(token);
                        break;
                    }
                }
            }
            if (token.getPriority() != 0)
                token.incrementPriority();
        }
    }

}
