package org.gargiolang.tokenizer.tokens.operators;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.exception.tokenization.UnrecognizedOperatorException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenLine;
import org.gargiolang.tokenizer.tokens.TokenType;

import java.util.Map;

public enum ArithmeticOperator {

    ADD("+"),
    SUB("-"),
    INV("-"), // invert --> e.g. -a, -1 (unary operator)
    MUL("*"),
    DIV("/"),
    MOD("%"),
    POW("**"),
    INC("++"),
    DEC("--");

    /** Token used for performance reasons --> multiplying by -1 can be fairly common */
    public static Token minusOne = new Token(TokenType.NUM, -1);

    private final String repr;

    private final static Map<ArithmeticOperator, Byte> priorities = Map.of(
            ADD, (byte) 3,
            SUB, (byte) 3,
            INV, (byte) 4, // same priority as multiplication since it is actually a multiplication by -1
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

        TokenLine line = interpreter.getLine();
        Token operator = interpreter.getCurrentToken();

        switch ((ArithmeticOperator) operator.getValue()) {
            case ADD -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.add(b));

                line.remove(a);
                line.remove(b);
            }
            case SUB -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                // multiply token by -1
                line.replace(operator, a.subtract(b));

                line.remove(a);
                line.remove(b);
            }
            case MUL -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.multiply(b));

                line.remove(a);
                line.remove(b);
            }
            case DIV -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.divide(b));

                line.remove(a);
                line.remove(b);
            }
            case MOD -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.mod(b));

                line.remove(a);
                line.remove(b);
            }
            case POW -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.power(b));

                line.remove(a);
                line.remove(b);
            }
            case INC -> {
                operator.getPrev().increment();
                line.remove(operator);
            }
            case DEC -> {
                operator.getPrev().decrement();
                line.remove(operator);
            }
            case INV -> {
                Token a = operator.getNext();
                line.replace(a, a.multiply(minusOne));
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
