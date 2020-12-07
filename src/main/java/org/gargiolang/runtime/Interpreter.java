package org.gargiolang.runtime;

import org.gargiolang.exception.evaluation.EvaluationException;
import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.tokenizer.tokens.operators.ArithmeticOperator;
import org.gargiolang.tokenizer.tokens.operators.AssignmentOperator;
import org.gargiolang.tokenizer.tokens.operators.LogicalOperator;
import org.gargiolang.tokenizer.tokens.operators.Parenthesis;
import org.gargiolang.tokenizer.tokens.*;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<TokenLine> tokens;

    // line that is currently being executed
    private int lineIndex = 0;
    private TokenLine line;
    // token that is currently being evaluated
    private Token currentToken;

    private boolean blockCurrentToken = false;


    public Interpreter(Runtime runtime, LinkedList<TokenLine> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }


    /**
     * The interpreter will not search for the highest priority token in the next iteration
     */
    public void blockCurrentToken() {
        this.blockCurrentToken = true;
    }


    public LinkedList<TokenLine> getTokens() {
        return tokens;
    }


    public Token getCurrentToken() {
        return currentToken;
    }


    /**
     * Warning: does not check if token is actually in the line
     *
     * @param token the token to set as current token
     */
    public void setCurrentToken(Token token) {
        this.currentToken = token;
    }


    public TokenLine getLine() {
        return line;
    }

    public TokenLine getLine(int lineIndex) throws IndexOutOfBoundsException {
        if (lineIndex == tokens.size() || lineIndex < 0)
            throw new IndexOutOfBoundsException("Line index out of bounds: Index: " + lineIndex + ", Size: " + tokens.size());
        return tokens.get(lineIndex);
    }


    public TokenLine getLineFrom(int lineIndex, Token fromToken) throws IndexOutOfBoundsException {
        TokenLine tempLine = this.getLine(lineIndex);
        return tempLine.subList(fromToken, tempLine.getLast());
    }


    /**
     * Warning: dangerous method
     *
     * @param line the line to be set as current line
     */
    public void setLine(TokenLine line) {
        this.line = line;
    }

    public void setLine(TokenLine line, int lineIndex, Token token) throws IndexOutOfBoundsException {
        this.line = line;
        this.setLineIndex(lineIndex);
        this.currentToken = token;
    }

    public void setLine(int lineIndex) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = tokens.get(lineIndex).copy();
    }


    public void setLineFrom(int lineIndex, Token fromToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = this.getLine(lineIndex);
        this.line = this.line.subList(fromToken, this.line.getLast()).copy();
    }


    public void setLineBetween(int lineIndex, Token fromToken, Token untilToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = this.getLine(lineIndex).subList(fromToken, untilToken).copy();
    }


    public void setLineUntil(int lineIndex, Token untilToken) throws IndexOutOfBoundsException {
        this.setLineIndex(lineIndex);
        this.line = tokens.get(lineIndex);
        this.line = this.line.subList(this.line.getFirst(), untilToken).copy();
    }


    public int getLineIndex() {
        return lineIndex;
    }


    /**
     * Sets the interpreter's current line to the one specified by the given index
     *
     * @param lineIndex the index of the line to be set
     * @throws IndexOutOfBoundsException if the specified lineIndex is out of bounds
     */
    public void setLineIndex(int lineIndex) throws IndexOutOfBoundsException {
        // check is given lineIndex exceeds the number of lines
        if (lineIndex > tokens.size() || lineIndex < 0)
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

        for ( ; this.lineIndex != eof; this.lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            this.line = tokens.get(lineIndex).copy();

            this.executeLine();

        } // end of script evaluation

    }


    /**
     * Executes the current line in the interpreter's environment and returns the evaluated line
     *
     * @throws EvaluationException if an error occurs during the line execution
     * @return returns the evaluated line
     */
    public TokenLine executeLine() throws EvaluationException, ReflectiveOperationException {

        while (!line.isEmpty()) {

            if (blockCurrentToken) blockCurrentToken = false;
            else currentToken = line.highestPriority();


            // if no more token to evaluate --> break out of the loop
            if (currentToken.getPriority() < 1) { // priority less or equal to 0, but faster than the double comparison
                break;
            }

            switch (currentToken.getType())
            {
                case LOGICAL_OPERATOR -> LogicalOperator.evaluate(this);

                case ARITHMETIC_OPERATOR -> ArithmeticOperator.evaluate(this);

                case ASSIGNMENT_OPERATOR -> AssignmentOperator.evaluate(this);

                case PAREN -> Parenthesis.evaluate(this);

                case KEYWORD -> Keyword.evaluate(this);

                case SCOPE -> Scope.evaluate(this);

                case FUNC -> Function.evaluate(this);

                case CALL -> Call.evaluate(this);

                default -> throw new EvaluationException("Could not evaluate token " + currentToken);
            }


        } // end of line evaluation


        return line;
    }
}
