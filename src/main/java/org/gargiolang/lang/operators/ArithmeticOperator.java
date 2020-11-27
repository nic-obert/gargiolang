package org.gargiolang.lang.operators;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.exception.parsing.UnrecognizedOperatorException;
import org.gargiolang.lang.Token;
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

    private final static Map<ArithmeticOperator, Byte> priorities = Map.of(
            ADD, (byte) 3,
            SUB, (byte) 3,
            MUL, (byte) 4,
            DIV, (byte) 4,
            MOD, (byte) 3,
            POW, (byte) 5,
            INC, (byte) 8,
            DEC, (byte) 8
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

        System.out.println("LINE: " + line);

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
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.power(b));

                line.remove(a);
                line.remove(b);
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
    }


    public static ArithmeticOperator fromString(String repr) throws UnrecognizedOperatorException {
        for (ArithmeticOperator operator : values()) {
            if (operator.getRepr().equals(repr))
                return operator;
        }

        throw new UnrecognizedOperatorException("Operator '" + repr + "' is not recognized");
    }
}
