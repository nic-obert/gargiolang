package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;

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


    public static ArithmeticOperator fromString(String repr) throws GargioniException {
        for (ArithmeticOperator operator : values()) {
            if (operator.getRepr().equals(repr))
                return operator;
        }

        throw new GargioniException("Operator '" + repr + "' is not recognized");
    }
}
