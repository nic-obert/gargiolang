package org.gargiolang.lang;

import org.gargiolang.lang.exception.evaluation.EvaluationException;
import org.gargiolang.lang.exception.parsing.UnrecognizedOperatorException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;
import java.util.Map;

public enum LogicalOperator {

    GR(">"),
    LS("<"),
    GRE(">="),
    LSE("<="),
    EQ("=="),
    AND("&&"),
    OR("||"),
    NOT("!"),
    NE("!=");

    private final String repr;

    //operators like && and || will have a lower priority
    private final static Map<LogicalOperator, Byte> priorities = Map.of(
            AND,    (byte) 1,
            OR,     (byte) 1,
            GR,     (byte) 2,
            LS,     (byte) 2,
            GRE,    (byte) 2,
            LSE,    (byte) 2,
            EQ,     (byte) 2,
            NE,     (byte) 2,
            NOT,    (byte) 9
    );

    private static final char[] chars = {'>', '<', '!', '&', '|'};


    public static boolean isLogicalOperator(char ch) {
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


    LogicalOperator(String repr) {
        this.repr = repr;
    }


    public static void evaluate(Interpreter interpreter) throws EvaluationException {

        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        Token operator = line.get(currentTokenIndex);


        switch ((LogicalOperator) operator.getValue()) {
            case GR -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.greaterThan(b));

                line.remove(a);
                line.remove(b);
            }

            case LS -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.lessThan(b));

                line.remove(a);
                line.remove(b);
            }

            case EQ -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.equalsTo(b));

                line.remove(a);
                line.remove(b);
            }

            case OR -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, Variable.Type.or(a, b));

                line.remove(a);
                line.remove(b);
            }

            case AND -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, Variable.Type.and(a, b));

                line.remove(a);
                line.remove(b);
            }

            case GRE -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, Variable.Type.greaterOrEquals(a, b));

                line.remove(a);
                line.remove(b);
            }

            case LSE -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, Variable.Type.lessOrEquals(a, b));

                line.remove(a);
                line.remove(b);
            }

            case NOT -> {
                Token a = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.not());

                line.remove(a);
            }

            case NE -> {
                Token a = line.get(currentTokenIndex - 1);
                Token b = line.get(currentTokenIndex + 1);

                line.set(currentTokenIndex, a.notEqualsTo(b));

                line.remove(a);
                line.remove(b);
            }
        }
    }

    public static LogicalOperator fromString(String repr) throws UnrecognizedOperatorException {
        for (LogicalOperator operator : values()) {
            if (operator.getRepr().equals(repr))
                return operator;
        }

        throw new UnrecognizedOperatorException("Operator '" + repr + "' is not recognized");
    }

}
