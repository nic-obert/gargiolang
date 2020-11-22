package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.lang.exception.evaluation.*;
import org.gargiolang.lang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Variable;

import java.util.LinkedList;
import java.util.Stack;

public enum Keyword {

    IF("if"),
    ELSE("else"),
    WHILE("while"),
    FOR("for"),

    GOTO("goto"),
    GOBACK("goback");

    private final String value;

    Keyword(String str){
        this.value = str;
    }

    public static boolean isKeyword(String str){
        for(Keyword keyword : Keyword.values()){
            if(keyword.value.equals(str)) return true;
        }
        return false;
    }


    public static void evaluate(Interpreter interpreter) throws BadTypeException, UnrecognizedTypeException, UndeclaredVariableException, IndexOutOfBoundsException, UndefinedLabelException, GoBackException {
        Keyword keyword = Keyword.valueOf(
                ((String) interpreter.getLine().get(interpreter.getCurrentTokenIndex()).getValue()).toUpperCase()
        );

        switch (keyword)
        {
            case IF:
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

                if (condition.getVarValue(interpreter.getRuntime()).equals(true)) {
                    interpreter.setLineFrom(scopes[0][0], scopes[0][1]);
                    interpreter.setCurrentTokenIndex(0);
                    // tell the interpreter not to search for the highest priority token next time
                    interpreter.blockCurrentTokenIndex();
                } else {
                    interpreter.setLineFrom(scopes[1][0], scopes[1][1] + 1);
                }

                break;

            case FOR:
                break;

            case ELSE:
                break;


            case GOTO:
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
                break;


            case WHILE:
                break;

            case GOBACK:
                Stack<Integer> gotoStack = interpreter.getRuntime().getGotoStack();
                if (gotoStack.isEmpty()) throw new GoBackException("Cannot go back more than this: goto stack is empty");
                interpreter.setLineIndex(gotoStack.pop() - 1);

                // finally remove the keyword
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
                break;
        }

    }


}
