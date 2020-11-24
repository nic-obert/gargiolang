package org.gargiolang.lang;

import org.gargiolang.lang.exception.evaluation.*;
import org.gargiolang.lang.exception.parsing.UnrecognizedOperatorException;
import org.gargiolang.runtime.Interpreter;

import java.util.LinkedList;
import java.util.Map;

public enum ArithmeticOperator {

    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    POW("**"),
    INC("++"),
    DEC("--");

    private final String repr;

    private final static Map<ArithmeticOperator, Integer> priorities = Map.of(
            ADD, 3,
            SUB, 3,
            MUL, 4,
            DIV, 4,
            MOD, 3,
            POW, 5,
            INC, 8,
            DEC, 8
    );


    private static final char[] chars = {'+', '-', '*', '/', '%'};


    public static boolean isArithmeticOperator(char ch) {
        for (char c : chars) {
            if (c == ch) return true;
        }
        return false;
    }


    public int getPriority() {
        return priorities.get(this);
    }


    public String getRepr() {
        return this.repr;
    }


    ArithmeticOperator(String repr) {
        this.repr = repr;
    }


    public static void evaluate(Interpreter interpreter) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException, ZeroDivisionException {

        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        Token operator = line.get(currentTokenIndex);

        switch ((ArithmeticOperator) operator.getValue()) {
            case ADD -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, a.add(b));

                line.remove(a);
                line.remove(b);
            }
            case SUB -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, a.subtract(b));

                line.remove(a);
                line.remove(b);
            }
            case MUL -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, a.multiply(b));

                line.remove(a);
                line.remove(b);
            }
            case DIV -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, a.divide(b));

                line.remove(a);
                line.remove(b);
            }
            case MOD -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, a.mod(b));

                line.remove(a);
                line.remove(b);
            }
            case POW -> {
                Token base = line.get(currentTokenIndex - 1);
                Token exponent = line.get(currentTokenIndex + 1);
                line.set(currentTokenIndex, base.power(exponent));

                line.remove(base);
                line.remove(exponent);
            }
            case INC -> {
                line.get(currentTokenIndex - 1).increment();
                line.remove(operator);
            }
            case DEC -> {
                line.get(currentTokenIndex - 1).decrement();
                line.remove(operator);
            }
        }


        /*

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

         */
    }


    public static ArithmeticOperator fromString(String repr) throws UnrecognizedOperatorException {
        for (ArithmeticOperator operator : values()) {
            if (operator.getRepr().equals(repr))
                return operator;
        }

        throw new UnrecognizedOperatorException("Operator '" + repr + "' is not recognized");
    }
}
