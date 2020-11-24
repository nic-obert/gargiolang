package org.gargiolang.lang;

import org.gargiolang.lang.exception.evaluation.*;
import org.gargiolang.lang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;
import java.util.Stack;

public enum Keyword {

    IF("if", (byte) 9),
    ELSE("else", (byte) 9),
    WHILE("while", (byte) 9),
    FOR("for", (byte) 9),

    GOTO("goto", (byte) 1),
    GOBACK("goback", (byte) 1);

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


    public static void evaluate(Interpreter interpreter) throws BadTypeException, UnrecognizedTypeException, UndeclaredVariableException, IndexOutOfBoundsException, UndefinedLabelException, GoBackException, OpenScopeException {
        Keyword keyword = (Keyword) interpreter.getLine().get(interpreter.getCurrentTokenIndex()).getValue();

        switch (keyword)
        {
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
