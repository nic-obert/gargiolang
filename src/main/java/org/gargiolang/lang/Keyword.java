package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.runtime.Interpreter;

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


    public static void evaluate(Interpreter interpreter) throws GargioniException {
        Keyword keyword = Keyword.valueOf(
                ((String) interpreter.getLine().get(interpreter.getCurrentTokenIndex()).getValue()).toUpperCase()
        );

        switch (keyword)
        {
            case IF:
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
                    // set the line of execution to the specified labelled line
                    interpreter.setLineIndex(
                            interpreter.getRuntime().getLabelTable().getLabel((String) toLineToken.getValue()) - 1); // -1 because the interpreter increments lineIndex by 1 in its for loop
                } else {
                    throw new GargioniException("Goto accepts only TokenType.TXT, but '" + toLineToken.getType() + "' was provided");
                }

                // finally remove the goto keyword and the label
                interpreter.getLine().remove(toLineToken);
                interpreter.getLine().remove(interpreter.getCurrentTokenIndex());
                break;


            case WHILE:
                break;

            case GOBACK:
                break;
        }

    }


}
