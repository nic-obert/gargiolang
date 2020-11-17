package org.gargiolang.runtime;

import org.gargiolang.lang.ArithmeticOperator;
import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.util.MathUtils;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<LinkedList<Token>> tokens;

    public Interpreter(Runtime runtime, LinkedList<LinkedList<Token>> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }

    public void execute() {
        int lineIndex = 0;

        for (LinkedList<Token> line : tokens) {
            int highestPriorityIndex = Token.getHighestPriority(line);
            Token highest = line.get(highestPriorityIndex);

            while (highest.getPriority() > 1){
                if (highest.getType().equals(Token.TokenType.ARITHMETIC_OPERATOR)) {
                    //TODO support string concatenation
                    ArithmeticOperator operator = ArithmeticOperator.valueOf(highest.getValue().toString());

                    Number a = MathUtils.createNumber(line.get(highestPriorityIndex - 1).getValue().toString());
                    Number b = MathUtils.createNumber(line.get(highestPriorityIndex + 1).getValue().toString());
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

                    line.set(highestPriorityIndex, new Token(Token.TokenType.NUM, result));
                    line.remove(highestPriorityIndex + 1);
                    line.remove(highestPriorityIndex - 1);

                    highestPriorityIndex = Token.getHighestPriority(line);
                    highest = line.get(highestPriorityIndex);
                }
            }

            if (highest.getType().equals(Token.TokenType.ASSIGNMENT_OPERATOR)) {

            }
        }

        System.out.println(tokens);
    }
}
