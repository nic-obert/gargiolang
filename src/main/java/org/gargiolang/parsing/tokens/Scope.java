package org.gargiolang.parsing.tokens;

import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.exception.evaluation.OpenScopeException;
import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;

public enum Scope {

    OPEN, CLOSE;

    public static void evaluate(Interpreter interpreter) {
        LinkedList<Token> line = interpreter.getLine();
        // remove token from line since it's been evaluated
        Token scope = line.remove(interpreter.getCurrentTokenIndex());

        // add or pop scope based on whether the token is { or }
        if (scope.getValue().equals(OPEN)) interpreter.getRuntime().getSymbolTable().pushScope();
        else interpreter.getRuntime().getSymbolTable().popScope();
    }

    /**
     *
     * @param interpreter the interpreter that is currently executing the script
     * @return position of the next matching scopes [[line, index], [line, index]]
     */
    public static int[][] findNextScope(Interpreter interpreter) throws IndexOutOfBoundsException, OpenScopeException {

        int[][] scopes = new int[2][2];
        int lineIndex = interpreter.getLineIndex();


        LinkedList<Token> line = interpreter.getLine();
        for (int scopeCount = 0; true; line = interpreter.getLine(lineIndex)) {

            for (Token token : line) {
                if (token.getType().equals(TokenType.SCOPE)) {

                    // if the scope is an open scope
                    if (token.getValue().equals(OPEN)) {
                        if (scopeCount == 0) {
                            scopes[0] = new int[]{lineIndex, interpreter.getLine(lineIndex).indexOf(token)};
                        }
                        scopeCount ++;
                    } else {
                        // if the scope is a closed scope
                        scopeCount --;
                        if (scopeCount == 0) {
                            // means that the matching closing scope has been found
                            scopes[1] = new int[]{lineIndex, interpreter.getLine(lineIndex).indexOf(token)};

                            // return the array of scope positions
                            return scopes;
                        }
                    }
                }
            } // end of for loop that searches the line

            // pass to the next line
            lineIndex ++;
            if (lineIndex == interpreter.getTokens().size()) throw new OpenScopeException("Scope is opened, but never closed");
        } // end of for loop that searches the whole script

        // no return statement since this line cannot be reached without throwing the above OpenScopeException
    }

}
