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

            //System.out.println(line);

            while (!line.isEmpty()){

                currentTokenIndex = Token.getHighestPriority(line);
                Token highest = line.get(currentTokenIndex);

                // if no more token to evaluate --> break out of the loop
                if (highest.getPriority() == 0) {
                    break;
                }

                switch (highest.getType())
                {
                    case ARITHMETIC_OPERATOR:
                        ArithmeticOperator.evaluate(this);
                        break;

                    case ASSIGNMENT_OPERATOR:
                        AssignmentOperator.evaluate(this);
                        break;

                    case PAREN:
                        Parenthesis.evaluate(this);
                        break;

                    case KEYWORD:
                        Keyword.evaluate(this);
                        break;

                    case SCOPE:
                        Scope.evaluate(this);
                        break;

                    default:
                        throw new GargioniException("Could not evaluate token " + highest);

                }


            } // end of line evaluation

            // print resulting line for debugging
            //System.out.println(line);
            //System.out.println(runtime.getSymbolTable());

        } // end of script evaluation

    }
}
