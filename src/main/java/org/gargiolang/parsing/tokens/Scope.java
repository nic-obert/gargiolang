package org.gargiolang.parsing.tokens;

import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.exception.evaluation.OpenScopeException;
import org.gargiolang.runtime.Interpreter;

public enum Scope {

    OPEN, CLOSE;

    public static void evaluate(Interpreter interpreter) {
        // remove token from line since it's been evaluated
        Token scope = interpreter.getCurrentToken();
        interpreter.getLine().remove(scope);
        
        // add or pop scope based on whether the token is { or }
        if (scope.getValue() == OPEN)
            interpreter.getRuntime().getSymbolTable().pushScope();
        else
            interpreter.getRuntime().getSymbolTable().popScope();
    }

    /**
     *
     * @param interpreter the interpreter that is currently executing the script
     * @return position of the next matching scopes [[line, index], [line, index]]
     */
    public static TokenBlock findNextScope(Interpreter interpreter) throws IndexOutOfBoundsException, OpenScopeException {

        int firstLine = 0;
        Token firstToken = null;

        int lineIndex = interpreter.getLineIndex();

        TokenLine line = interpreter.getLine();
        for (int scopeCount = 0; true; line = interpreter.getLine(lineIndex)) {

            Token lastToken = line.getLast().getNext();
            for (Token token = line.getFirst(); token != lastToken; token = token.getNext()) {
                if (token.getType() == TokenType.SCOPE) {

                    // if the scope is an open scope
                    if (token.getValue() == OPEN) {
                        if (scopeCount == 0) {
                            firstLine = lineIndex;
                            firstToken = token;
                        }
                        scopeCount ++;
                    } else {
                        // if the scope is a closed scope
                        scopeCount --;
                        if (scopeCount == 0) {
                            // means that the matching closing scope has been found
                            // return the token block
                            return new TokenBlock(firstLine, firstToken, lineIndex, token);
                        }
                    }
                }
            } // end of for loop that searches the line

            // pass to the next line
            lineIndex ++;
            if (lineIndex == interpreter.getTokens().size())
                throw new OpenScopeException("Scope is opened, but never closed");
        } // end of for loop that searches the whole script

        // no return statement since this line cannot be reached without throwing the above OpenScopeException
    }

}
