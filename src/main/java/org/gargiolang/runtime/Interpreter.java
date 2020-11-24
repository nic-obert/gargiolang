package org.gargiolang.runtime;

import org.gargiolang.lang.*;
import org.gargiolang.lang.exception.evaluation.EvaluationException;
import org.gargiolang.lang.exception.evaluation.IndexOutOfBoundsException;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<LinkedList<Token>> tokens;

    // line that is currently being executed
    private int lineIndex = 0;
    private LinkedList<Token> line;
    // token that is currently being evaluated
    private int currentTokenIndex;

    private boolean blockCurrentTokenIndex = false;

    public Interpreter(Runtime runtime, LinkedList<LinkedList<Token>> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }

    public void blockCurrentTokenIndex() {
        this.blockCurrentTokenIndex = true;
    }

    public LinkedList<LinkedList<Token>> getTokens() {
        return tokens;
    }

    public LinkedList<Token> getLine() {
        return line;
    }

    public void setLine(int lineIndex) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = (LinkedList<Token>) tokens.get(lineIndex).clone();
    }

    /**
     * Set the current line to be executed to the one specified.
     *
     * @param lineIndex the index of the line to jump to
     * @param fromToken the position from which to start the execution of the line
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineFrom(int lineIndex, int fromToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = new LinkedList<>(tokens.get(lineIndex).subList(fromToken, tokens.get(lineIndex).size()));
    }

    /**
     * Set the current line to be executed to the one specified.
     *
     * @param lineIndex the index of the line to jump to
     * @param untilToken the position where the line should stop
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineUntil(int lineIndex, int untilToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = new LinkedList<>(tokens.get(lineIndex).subList(0, untilToken));
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getCurrentTokenIndex() {
        return currentTokenIndex;
    }

    public void setCurrentTokenIndex(int currentTokenIndex) throws IndexOutOfBoundsException {
        if (currentTokenIndex > line.size() || currentTokenIndex < 0) throw new IndexOutOfBoundsException("Given index out of bounds: Index: " + currentTokenIndex + ", Size: " + line.size());
        this.currentTokenIndex = currentTokenIndex;
    }

    public void setLineIndex(int lineIndex) throws IndexOutOfBoundsException {
        // check is given lineIndex exceeds the number of lines
        if (lineIndex > tokens.size() || currentTokenIndex < 0) throw new IndexOutOfBoundsException("Line index out of bounds: Index: " + lineIndex + ", Size: " + tokens.size());
        this.lineIndex = lineIndex;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void execute() throws EvaluationException {
        int eof = tokens.size();

        for (; lineIndex < eof; lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            line = (LinkedList<Token>) tokens.get(lineIndex).clone();

            System.out.println(line);

            while (!line.isEmpty()) {

                if (blockCurrentTokenIndex) blockCurrentTokenIndex = false;
                else currentTokenIndex = Token.getHighestPriority(line);

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
                        throw new EvaluationException("Could not evaluate token " + highest);

                }


            } // end of line evaluation

            // print resulting line for debugging
            System.out.println(line);
            //System.out.println(runtime.getSymbolTable());

        } // end of script evaluation

    }
}
