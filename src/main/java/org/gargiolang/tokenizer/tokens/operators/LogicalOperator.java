package org.gargiolang.tokenizer.tokens.operators;

import org.gargiolang.exception.evaluation.EvaluationException;
import org.gargiolang.exception.tokenization.UnrecognizedOperatorException;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenLine;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.variable.Variable;

import java.util.Map;

public enum LogicalOperator {

    INCOMPLETE_AND("&"),
    INCOMPLETE_OR("|"),

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
        if (this == INCOMPLETE_AND || this == INCOMPLETE_OR) return 0;
        return priorities.get(this);
    }


    public String getRepr() {
        return this.repr;
    }


    LogicalOperator(String repr) {
        this.repr = repr;
    }


    public static void evaluate(Interpreter interpreter) throws EvaluationException {

        TokenLine line = interpreter.getLine();
        Token operator = interpreter.getCurrentToken();


        switch ((LogicalOperator) operator.getValue()) {
            case GR -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.greaterThan(b));

                line.remove(a);
                line.remove(b);
            }

            case LS -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.lessThan(b));

                line.remove(a);
                line.remove(b);
            }

            case EQ -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.equalsTo(b));

                line.remove(a);
                line.remove(b);
            }

            case OR -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, Variable.Type.or(a, b));

                line.remove(a);
                line.remove(b);
            }

            case AND -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, Variable.Type.and(a, b));

                line.remove(a);
                line.remove(b);
            }

            case GRE -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, Variable.Type.greaterOrEquals(a, b));

                line.remove(a);
                line.remove(b);
            }

            case LSE -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, Variable.Type.lessOrEquals(a, b));

                line.remove(a);
                line.remove(b);
            }

            case NOT -> {
                Token a = operator.getNext();

                line.replace(operator, a.not());

                line.remove(a);
            }

            case NE -> {
                Token a = operator.getPrev();
                Token b = operator.getNext();

                line.replace(operator, a.notEqualsTo(b));

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
