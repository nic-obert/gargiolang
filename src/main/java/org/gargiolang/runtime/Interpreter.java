package org.gargiolang.runtime;

import org.gargiolang.lang.*;
import org.gargiolang.exception.evaluation.EvaluationException;
import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.lang.operators.ArithmeticOperator;
import org.gargiolang.lang.operators.AssignmentOperator;
import org.gargiolang.lang.operators.LogicalOperator;
import org.gargiolang.lang.operators.Parenthesis;

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


    /**
     * The interpreter will not search for the highest priority token in the next iteration
     */
    public void blockCurrentTokenIndex() {
        this.blockCurrentTokenIndex = true;
    }


    public LinkedList<LinkedList<Token>> getTokens() {
        return tokens;
    }


    public LinkedList<Token> getLine() {
        return line;
    }

    public LinkedList<Token> getLine(int lineIndex) throws IndexOutOfBoundsException {
        if (lineIndex == tokens.size() || currentTokenIndex < 0) throw new IndexOutOfBoundsException("Line index out of bounds: Index: " + lineIndex + ", Size: " + tokens.size());
        return tokens.get(lineIndex);
    }

    /**
     * Returns the specified line sublist of tokens
     *
     * @param lineIndex index of the line to get
     * @param fromToken token from which to start the line
     * @return specified sublist of tokens
     * @throws IndexOutOfBoundsException if specified lineIndex or fromToken are out of bounds
     */
    public LinkedList<Token> getLineFrom(int lineIndex, int fromToken) throws IndexOutOfBoundsException {
        if (fromToken == this.getLine(lineIndex).size() || fromToken < 0)
            throw new IndexOutOfBoundsException("Token index out of bounds: Index: " + fromToken + ", Size: " + this.getLine(lineIndex).size());
        return new LinkedList<>(this.getLine(lineIndex).subList(fromToken, this.getLine(lineIndex).size()));
    }


    /**
     * Sets the current line to the one provided and pdates lineIndex and currentTokenIndex.
     * This could also be referred as setting the interpreter's state.
     *
     * @param line the line to be set as current line
     * @param lineIndex the index of the line inside the token list
     * @param currentTokenIndex the token that is currently being executed
     * @throws IndexOutOfBoundsException if the lineIndex is out of bounds with respect to the token list
     */
    public void setLine(LinkedList<Token> line, int lineIndex, int currentTokenIndex) throws IndexOutOfBoundsException {
        this.line = line;
        this.setLineIndex(lineIndex);
        this.setCurrentTokenIndex(currentTokenIndex);
    }


    public void setLine(int lineIndex) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);

        this.line = (LinkedList<Token>) tokens.get(lineIndex).clone();
    }

    /**
     * Set the current line to be executed to the one specified.
     *
     * @param lineIndex the index of the line to execute to
     * @param fromToken the position from which to start the execution of the line
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineFrom(int lineIndex, int fromToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = new LinkedList<>(this.getLine(lineIndex).subList(fromToken, this.getLine(lineIndex).size()));
    }


    /**
     * Set the current line to be executed to the one specified
     *
     * @param lineIndex the index of the line to execute
     * @param fromToken the position from which to start the execution of the line
     * @param untilToken the position up to which to execute the line
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineBetween(int lineIndex, int fromToken, int untilToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = new LinkedList<>(this.getLine(lineIndex).subList(fromToken, untilToken));
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
        if (currentTokenIndex == line.size() || currentTokenIndex < 0)
            throw new IndexOutOfBoundsException("Given index out of bounds: Index: " + currentTokenIndex + ", Size: " + line.size());
        this.currentTokenIndex = currentTokenIndex;
    }


    /**
     * Sets the interpreter's current line to the one specified by the given index
     *
     * @param lineIndex the index of the line to be set
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineIndex(int lineIndex) throws IndexOutOfBoundsException {
        // check is given lineIndex exceeds the number of lines
        if (lineIndex > tokens.size() || currentTokenIndex < 0)
            throw new IndexOutOfBoundsException("Line index out of bounds: Index: " + lineIndex + ", Size: " + tokens.size());
        this.lineIndex = lineIndex;
    }


    public Runtime getRuntime() {
        return runtime;
    }


    /**
     * Executes the script in the interpreter's environment
     *
     * @throws EvaluationException if an error occurs during the script execution
     */
    public void execute() throws EvaluationException, ReflectiveOperationException {
        int eof = tokens.size();

        for (; lineIndex < eof; lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            this.line = (LinkedList<Token>) tokens.get(lineIndex).clone();

            this.executeLine();

        } // end of script evaluation

    }


    /**
     * Executes the current line in the interpreter's environment and returns the evaluated line
     *
     * @throws EvaluationException if an error occurs during the line execution
     * @return returns the evaluated line
     */
    public LinkedList<Token> executeLine() throws EvaluationException, ReflectiveOperationException {
        // print the line for debugging
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
                case LOGICAL_OPERATOR -> LogicalOperator.evaluate(this);

                case ARITHMETIC_OPERATOR -> ArithmeticOperator.evaluate(this);

                case ASSIGNMENT_OPERATOR -> AssignmentOperator.evaluate(this);

                case PAREN -> Parenthesis.evaluate(this);

                case KEYWORD -> Keyword.evaluate(this);

                case SCOPE -> Scope.evaluate(this);

                case FUNC -> Function.evaluate(this);

                case CALL -> Call.evaluate(this);

                default -> throw new EvaluationException("Could not evaluate token " + highest);
            }


        } // end of line evaluation

        // print resulting line for debugging
        //System.out.println(line);
        //System.out.println(runtime.getSymbolTable());

        return line;
    }
}
