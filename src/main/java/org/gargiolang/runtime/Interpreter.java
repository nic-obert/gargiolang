package org.gargiolang.runtime;

import org.gargiolang.lang.ArithmeticOperator;
import org.gargiolang.lang.Parenthesis;
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

    public void execute() throws GargioniException {
        int lineIndex = 0;
        int eof = tokens.size();

        for (; lineIndex < eof; lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            LinkedList<Token> line = (LinkedList<Token>) tokens.get(lineIndex).clone();

            while (true){

                int highestPriorityIndex = Token.getHighestPriority(line);
                Token highest = line.get(highestPriorityIndex);

                // if no more token to evaluate --> break out of the loop
                if (highest.getPriority() == 0) {
                    break;
                }


                if (highest.getType().equals(Token.TokenType.ARITHMETIC_OPERATOR)) {
                    // TODO support string concatenation (maybe operator overloading in the Token class?)
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
                }


                else if (highest.getType().equals(Token.TokenType.ASSIGNMENT_OPERATOR)) {
                    // TODO implement assignment operator
                }


                else if (highest.getType().equals(Token.TokenType.PAREN) && highest.getValue().equals(Parenthesis.OPEN)) {
                    // number of opening parenthesis encountered
                    int parenCount = 1;
                    for (int counter = highestPriorityIndex + 1; true; counter++) {
                        Token token = line.get(counter);

                        if (token.getType().equals(Token.TokenType.PAREN)) {
                            if (token.getValue().equals(Parenthesis.OPEN)) {
                                parenCount ++;
                            } else {
                                parenCount --;
                                // check if reached matching closing parenthesis
                                if (parenCount == 0) {
                                    // remove closing parenthesis token
                                    line.remove(counter);
                                    break;
                                }
                            }
                        }
                        if (token.getPriority() != 0) token.incrementPriority();
                    }

                    // remove opening parenthesis token
                    line.remove(highestPriorityIndex);
                }


                else {
                    throw new GargioniException("Could not evaluate token " + highest);
                }


            } // end of line evaluation

            // print resulting line for debugging
            System.out.println(line);

        } // end of script evaluation

    }
}
