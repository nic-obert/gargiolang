package org.gargiolang.runtime;

import org.gargiolang.lang.*;
import org.gargiolang.lang.exception.GargioniException;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<LinkedList<Token>> tokens;

    // line that is currently being executed
    private int lineIndex = 0;
    private LinkedList<Token> line;
    // token that is currently being evaluated
    private int currentTokenIndex;

    public Interpreter(Runtime runtime, LinkedList<LinkedList<Token>> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }


    public LinkedList<Token> getLine() {
        return line;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getCurrentTokenIndex() {
        return currentTokenIndex;
    }

    public void setLineIndex(int lineIndex) throws GargioniException {
        // check is given lineIndex exceeds the number of lines
        if (lineIndex > tokens.size()) throw new GargioniException("Line index out of bounds: " + lineIndex + " > " + tokens.size());
        this.lineIndex = lineIndex;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void execute() throws GargioniException {
        int eof = tokens.size();

        for (; lineIndex < eof; lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            line = (LinkedList<Token>) tokens.get(lineIndex).clone();

            while (!line.isEmpty()){

                currentTokenIndex = Token.getHighestPriority(line);
                Token highest = line.get(currentTokenIndex);

                // if no more token to evaluate --> break out of the loop
                if (highest.getPriority() == 0) {
                    break;
                }

                if (highest.getType().equals(Token.TokenType.ARITHMETIC_OPERATOR)) {
                    ArithmeticOperator.evaluate(this);
                }
                else if (highest.getType().equals(Token.TokenType.ASSIGNMENT_OPERATOR)) {
                    AssignmentOperator.evaluate(this);
                }
                else if (highest.getType().equals(Token.TokenType.PAREN) && highest.getValue().equals(Parenthesis.OPEN)) {
                    Parenthesis.evaluate(this);
                }
                else if (highest.getType().equals(Token.TokenType.KEYWORD)) {
                    Keyword.evaluate(Keyword.valueOf(((String) highest.getValue()).toUpperCase()), this);
                }
                else {
                    throw new GargioniException("Could not evaluate token " + highest);
                }


            } // end of line evaluation

            // print resulting line for debugging
            System.out.println(line);

        } // end of script evaluation

    }
}
