package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.lang.exception.evaluation.*;
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
    EQ("==");

    private final String repr;

    //operators like && and || will have a lower priority
    private final static Map<LogicalOperator, Integer> priorities = Map.of(
            GR, 2,
            LS, 2,
            GRE, 2,
            LSE, 2,
            EQ, 2
    );

    private static final char[] chars = {'>', '<', '='};


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

        Token a = line.get(currentTokenIndex - 1);
        Token b = line.get(currentTokenIndex + 1);

        boolean valid = (a.getVarType(interpreter.getRuntime()).equals(Variable.Type.FLOAT) ||
                a.getVarType(interpreter.getRuntime()).equals(Variable.Type.INT)) &&
                        (b.getVarType(interpreter.getRuntime()).equals(Variable.Type.FLOAT) ||
                b.getVarType(interpreter.getRuntime()).equals(Variable.Type.INT));

        if(!valid) throw new EvaluationException("You can only compare numbers");

        switch ((LogicalOperator) operator.getValue()) {
            case GR -> {
                line.set(currentTokenIndex, a.isMore(b));

                line.remove(a);
                line.remove(b);
            }

            case LS -> {
                line.set(currentTokenIndex, a.isLess(b));

                line.remove(a);
                line.remove(b);
            }

            case EQ -> {
                line.set(currentTokenIndex, a.isEquals(b));

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
