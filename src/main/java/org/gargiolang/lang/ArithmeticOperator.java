package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.util.MathUtils;

import java.util.LinkedList;
import java.util.Map;

public enum ArithmeticOperator {

    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    POW("**"),
    INC("++"),
    DEC("--");

    private final String repr;

    private final static Map<ArithmeticOperator, Integer> priorities = Map.of(
            ADD, 3,
            SUB, 3,
            MUL, 4,
            DIV, 4,
            POW, 5,
            INC, 8,
            DEC, 8
    );


    private static char[] chars = {'+', '-', '*', '/'};


    public static boolean isArithmeticOperator(char ch) {
        for (char c : chars) {
            if (c == ch) return true;
        }
        return false;
    }


    public static int getPriority(ArithmeticOperator operator) {
        return operator.getPriority();
    }

    public int getPriority() {
        return priorities.get(this);
    }


    public String getRepr() {
        return this.repr;
    }

    public static String getRepr(ArithmeticOperator operator) {
        return operator.getRepr();
    }


    ArithmeticOperator(String repr) {
        this.repr = repr;
    }


    public static void evaluate(Interpreter interpreter) {
        LinkedList<Token> line = interpreter.getLine();
        Token highest = line.get(interpreter.getCurrentTokenIndex());
        int currentTokenIndex = interpreter.getCurrentTokenIndex();

        ArithmeticOperator operator = ArithmeticOperator.valueOf(highest.getValue().toString());

        Number a = MathUtils.createNumber(line.get(currentTokenIndex - 1).getValue().toString());
        Number b = MathUtils.createNumber(line.get(currentTokenIndex + 1).getValue().toString());
        Number result = 0;

        switch (operator) {
            case ADD: {
                result = MathUtils.addNumbers(a, b);
                break;
            }
            case SUB: {
                result = MathUtils.subtractNumbers(a, b);
                break;
            }
            case MUL: {
                result = MathUtils.multiplyNumbers(a, b);
                break;
            }
            case DIV: {
                result = MathUtils.divideNumbers(a, b);
                break;
            }
            case POW: {
                result = MathUtils.elevateNumbers(a, b);
                break;
            }
        }

        line.set(currentTokenIndex, new Token(Token.TokenType.NUM, result));
        line.remove(currentTokenIndex + 1);
        line.remove(currentTokenIndex - 1);
    }


    public static ArithmeticOperator fromString(String repr) throws GargioniException {
        for (ArithmeticOperator operator : values()) {
            if (operator.getRepr().equals(repr))
                return operator;
        }

        throw new GargioniException("Operator '" + repr + "' is not recognized");
    }
}
