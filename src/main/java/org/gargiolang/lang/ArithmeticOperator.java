package org.gargiolang.lang;

import java.util.Map;

public enum ArithmeticOperator {

    ADD, SUB, MUL, DIV, POW, INC, DEC;

    private final static Map<String, Integer> priorities = Map.of(
            "+", 3,
            "-", 3,
            "*", 4,
            "/", 4,
            "**", 5,
            "++", 8,
            "--", 8
    );

    private static char[] chars = {'+', '-', '*', '/'};


    public static boolean isArithmeticOperator(char ch) {
        for (char c : chars) {
            if (c == ch) return true;
        }
        return false;
    }

    public static int getPriority(String operator) {
        return priorities.get(operator);
    }

}
