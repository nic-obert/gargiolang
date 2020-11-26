package org.gargiolang.lang;

import org.gargiolang.lang.exception.evaluation.BadTypeException;
import org.gargiolang.lang.exception.evaluation.EvaluationException;
import org.gargiolang.lang.exception.evaluation.GoBackException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;
import java.util.Stack;

public enum Keyword {

    GOTO("goto", (byte) 1),
    GOBACK("goback", (byte) 1),

    BREAK("break", (byte) 1),
    CONTINUE("continue", (byte) 1),

    IF("if", (byte) 9),
    ELSE("else", (byte) 9),

    WHILE("while", (byte) 10),
    FOR("for", (byte) 10);

    private final String value;
    private final byte priority;


    Keyword(String str, byte priority){
        this.value = str;
        this.priority = priority;
    }


    public static boolean isKeyword(String str){
        for(Keyword keyword : Keyword.values()){
            if(keyword.value.equals(str)) return true;
        }
        return false;
    }


    public int getPriority() {
        return this.priority;
    }


    public static void evaluate(Interpreter interpreter) throws EvaluationException {
        Keyword keyword = (Keyword) interpreter.getLine().get(interpreter.getCurrentTokenIndex()).getValue();

        switch (keyword)
        {
            case BREAK -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of BREAK
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(Token.TokenType.KEYWORD, Keyword.BREAK);
                token.setPriority(0);
                interpreter.getLine().add(token);
            }

            case CONTINUE -> {
                /*
                    - clear the current line
                    - create a token of type KEYWORD and value of CONTINUE
                    - set its priority to 0
                    - add it to the current line
                 */

                interpreter.getLine().clear();
                Token token = new Token(Token.TokenType.KEYWORD, Keyword.CONTINUE);
                token.setPriority(0);
                interpreter.getLine().add(token);
            }

            case IF -> {
                LinkedList<Token> line = interpreter.getLine();
                Token condition = line.get(interpreter.getCurrentTokenIndex() + 1);

                // check if condition is actually a boolean
                if (!condition.getVarType(interpreter.getRuntime()).equals(Variable.Type.BOOLEAN))
                    throw new BadTypeException("Condition is not a boolean: " + condition);

                // remove if keyword and condition
                line.remove(condition);
                line.remove(interpreter.getCurrentTokenIndex());

                // get the position of the next matching scopes
                int[][] scopes = Scope.findNextScope(interpreter);

                // check if the boolean condition is true
                if (condition.getVarValue(interpreter.getRuntime()).equals(true)) {
                    interpreter.setLineFrom(scopes[0][0], scopes[0][1]);
                    interpreter.setCurrentTokenIndex(0);
                    // tell the interpreter not to search for the highest priority token next time
                    interpreter.blockCurrentTokenIndex();
                }
                // whereas if the boolean condition is false
                else {
                    interpreter.setLineFrom(scopes[1][0], scopes[1][1] + 1);
                    // check if there is an else --> remove it
                    if (interpreter.getLine().getFirst().getValue().equals(ELSE)) {
                        interpreter.getLine().removeFirst();
                    }
                }
            }

            case FOR -> {
                /*
                    Algorithm:
                        - store the current number of scopes

                        - firstly push a new scope to the stack
                        - get the position of the code block to execute
                        - execute the first statement (e.g. int i = 0;)

                        - check the second boolean statement (e.g. i > 10;)
                        - execute code block in the next scope
                        - execute the third statement (e.g. i++;)
                        - repeat

                        - when the loop is broken --> pop the previously added scopes
                        - set line execution to after the while loop
                 */

                // store the current number of scopes
                int scopeCount = interpreter.getRuntime().getSymbolTable().scopeCount();

                // push a new scope to the stack
                interpreter.getRuntime().getSymbolTable().pushScope();

                // get the position of parenthesis
                int[][] parenthesis = Parenthesis.findNextParenthesis(interpreter);

                // get the position of the for loop's block
                int[][] forBlock = Scope.findNextScope(interpreter);
                /*
                    forBlock[][]'s structure:
                        [0][0] --> line of the opening scope
                        [0][1] --> index of the opening scope in its line
                        [1][0] --> line of the closing scope
                        [1][1] --> index of the closing scope in its line
                 */

                // store statement positions for ease of use
                int secondStatementLine = parenthesis[0][0] + 1;
                int thirdStatementLine = parenthesis[0][0] + 2;

                // execute the first statement (e.g. int i = 0;)
                interpreter.setLineFrom(parenthesis[0][0], parenthesis[0][1] + 1);
                interpreter.executeLine();

                // check if the second statement evaluates to a boolean (e.g. i < 10;)
                interpreter.setLine(secondStatementLine);
                interpreter.executeLine();
                if (interpreter.getLine().size() == 0 || !interpreter.getLine().getFirst().getType().equals(Token.TokenType.BOOL))
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + interpreter.getLine(secondStatementLine));


                /*
                    - check the boolean statement
                    - execute the code block
                    - execute the third statement
                 */
                boolean forLooping = true;
                while (forLooping) {

                    // check the second boolean statement (the loop's condition e.g. i < 10;)
                    interpreter.setLine(secondStatementLine);
                    if (interpreter.executeLine().getFirst().getVarValue(interpreter.getRuntime()).equals(false)) break;

                    // execute the code block

                    // set the line execution to the beginning of the for loop
                    interpreter.setLineFrom(forBlock[0][0], forBlock[0][1]);
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line and store result
                        interpreter.executeLine();

                        // check first if line is not empty
                        if (interpreter.getLine().size() != 0) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType().equals(Token.TokenType.KEYWORD)) {
                                switch ((Keyword) result.getValue()) {
                                    case BREAK -> {
                                        forLooping = false;
                                        executingBlock = false;
                                    }
                                    case CONTINUE -> executingBlock = false;
                                }
                            }
                        }

                        // if the next line is the last line of the loop --> include only until the closing scope
                        if (interpreter.getLineIndex() == forBlock[1][0] - 1) {
                            interpreter.setLineUntil(forBlock[1][0], forBlock[1][1] + 1);
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == forBlock[1][0]) {
                            executingBlock = false;
                        }
                        // otherwise just jump to the next line
                        else {
                            interpreter.setLine(interpreter.getLineIndex() + 1);
                        }

                    } // ---------------------------- end of code block

                    // execute third statement (e.g. i++;)
                    interpreter.setLineUntil(thirdStatementLine, parenthesis[1][1]);
                    interpreter.executeLine();

                } // ----------------------------------  end of for loop


                // pop previously added scopes
                interpreter.getRuntime().getSymbolTable().popScopes(interpreter.getRuntime().getSymbolTable().scopeCount() - scopeCount);

                // set the line execution to after the for loop
                interpreter.setLineFrom(forBlock[1][0], forBlock[1][1] + 1);

            }

            case ELSE -> {
                // jump to the end of the scope (do not execute this block of code)
                int[][] scopes = Scope.findNextScope(interpreter);
                interpreter.setLineFrom(scopes[1][0], scopes[1][1] + 1);
            }

            case GOTO -> {
                // get the next token in line
                Token toLineToken = interpreter.getLine().get(interpreter.getCurrentTokenIndex() + 1);
                // check if the label to go to is of type Token.TokenType.TXT
                if (toLineToken.getType().equals(Token.TokenType.TXT)) {
                    // push the lineIndex from where goto has been called to the gotoStack
                    interpreter.getRuntime().getGotoStack().push(interpreter.getLineIndex());
                    // set the line of execution to the specified labelled line
                    interpreter.setLineIndex(
                            interpreter.getRuntime().getLabelTable().getLabel((String) toLineToken.getValue()) - 1); // -1 because the interpreter increments lineIndex by 1 in its for loop
                } else {
                    throw new BadTypeException("Goto accepts only TokenType.TXT, but '" + toLineToken.getType() + "' was provided");
                }

                // finally remove the goto keyword and the label
                interpreter.getLine().remove(toLineToken);
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
            }

            case WHILE -> {
                /*
                    - store the current number of scopes

                    - first push a new scope to the stack
                    - check the boolean condition
                    - execute the code block

                    - pop the previously added scope
                    - set line execution to after the while loop
                 */

                // store the current number of scopes
                int scopeCount = interpreter.getRuntime().getSymbolTable().scopeCount();

                // push new scope
                interpreter.getRuntime().getSymbolTable().pushScope();

                // get the loop's condition statement
                int[][] parenthesis = Parenthesis.findNextParenthesis(interpreter);

                // get the code block
                int[][] whileBlock = Scope.findNextScope(interpreter);

                // check if loop's condition statement evaluates to a boolean
                interpreter.setLineBetween(parenthesis[0][0], parenthesis[0][1] + 1, parenthesis[1][1]);
                interpreter.executeLine();
                if (interpreter.getLine().size() == 0 || !interpreter.getLine().getFirst().getType().equals(Token.TokenType.BOOL))
                    throw new BadTypeException("Statement does not evaluate to a boolean: " + interpreter.getLine(parenthesis[0][0]).subList(parenthesis[0][1], parenthesis[1][1]));


                boolean whileLooping = true;
                while (whileLooping) {

                    // check the loop's condition
                    interpreter.setLineBetween(parenthesis[0][0], parenthesis[0][1] + 1, parenthesis[1][1]);
                    if (interpreter.executeLine().getFirst().getValue().equals(false)) break;

                    // execute the code block

                    // set the line execution to the beginning of the code block
                    interpreter.setLineFrom(whileBlock[0][0], whileBlock[0][1]);
                    boolean executingBlock = true;
                    while (executingBlock) {

                        // execute the current line
                        interpreter.executeLine();

                        // check first if the line is not empty
                        if (interpreter.getLine().size() != 0) {
                            // get the result of line execution
                            Token result = interpreter.getLine().getFirst();

                            // if the result is a BREAK or CONTINUE keyword --> act consequently
                            if (result.getType().equals(Token.TokenType.KEYWORD)) {
                                switch ((Keyword) result.getValue()) {
                                    case BREAK -> {
                                        whileLooping = false;
                                        executingBlock = false;
                                    }
                                    case CONTINUE -> executingBlock = false;
                                }
                            }
                        }

                        // if the next line is the last line of the loop --> include only until the closing scope
                        if (interpreter.getLineIndex() == whileBlock[1][0] - 1) {
                            interpreter.setLineUntil(whileBlock[1][0], whileBlock[1][1] + 1);
                        }
                        // if the current line is the last line of the loop --> set executingBlock to false to break the loop
                        else if (interpreter.getLineIndex() == whileBlock[1][0]) {
                            executingBlock = false;
                        }
                        // otherwise just jump to the next line
                        else {
                            interpreter.setLine(interpreter.getLineIndex() + 1);
                        }

                    } // --------------------- end of code block

                } // -------------------------- end of while loop

                // pop previously added scopes
                interpreter.getRuntime().getSymbolTable().popScopes(interpreter.getRuntime().getSymbolTable().scopeCount() - scopeCount);

                // set line execution to after the while loop
                interpreter.setLineFrom(whileBlock[1][0], whileBlock[1][1] + 1);
            }

            case GOBACK -> {
                Stack<Integer> gotoStack = interpreter.getRuntime().getGotoStack();
                if (gotoStack.isEmpty())
                    throw new GoBackException("Cannot go back more than this: goto stack is empty");
                interpreter.setLineIndex(gotoStack.pop() - 1);

                // finally remove the keyword
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
            }
        }

    }


}
