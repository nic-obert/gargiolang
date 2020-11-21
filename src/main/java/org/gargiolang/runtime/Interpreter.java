package org.gargiolang.runtime;

import org.gargiolang.lang.ArithmeticOperator;
import org.gargiolang.lang.Keyword;
import org.gargiolang.lang.Parenthesis;
import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.util.MathUtils;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<LinkedList<Token>> tokens;

    // line that is currently being executed
    private int lineIndex = 0;
    private LinkedList<Token> line;
    // token that is currently being evaluated
    private int currentTokenIndex;

    public Interpreter(Runtime runtime, LinkedList<LinkedList<Token>> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }


    public LinkedList<Token> getLine() {
        return line;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getCurrentTokenIndex() {
        return currentTokenIndex;
    }

    public void setLineIndex(int lineIndex) throws GargioniException {
        // check is given lineIndex exceeds the number of lines
        if (lineIndex > tokens.size()) throw new GargioniException("Line index out of bounds: " + lineIndex + " > " + tokens.size());
        this.lineIndex = lineIndex;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void execute() throws GargioniException {
        int eof = tokens.size();

        for (; lineIndex < eof; lineIndex ++) {
            // here a copy of the line is needed, not its reference (for goto, function calls, loops and repeating code)
            line = (LinkedList<Token>) tokens.get(lineIndex).clone();

            while (!line.isEmpty()){

                currentTokenIndex = Token.getHighestPriority(line);
                Token highest = line.get(currentTokenIndex);

                // if no more token to evaluate --> break out of the loop
                if (highest.getPriority() == 0) {
                    break;
                }


                if (highest.getType().equals(Token.TokenType.ARITHMETIC_OPERATOR)) {
                    // TODO support string concatenation (maybe operator overloading in the Token class?)
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
                }


                else if (highest.getType().equals(Token.TokenType.ASSIGNMENT_OPERATOR)) {
                    //Code below will be implemented soon
                    SymbolTable table = runtime.getSymbolTable();

                    System.out.println(currentTokenIndex);

                    Token after = line.get(currentTokenIndex + 1);
                    Token before = line.get(currentTokenIndex - 1);

                    if(after == null || before == null){
                        throw new GargioniException("Invalid statement");
                    }

                    if(table.getVariable((String) before.getValue()) == null){
                        if(line.size() == 3){
                            throw new GargioniException("Type isn't provided");
                        }

                        table.addVariable((String) before.getValue(), new Variable(after.getValue(), Variable.Type.valueOf(line.get(currentTokenIndex - 2).getValue().toString().toUpperCase()), Accessibility.PUBLIC));

                        //Lo ripeto due volte perchè mi bestemmia addosso per l'ordine
                        line.remove(currentTokenIndex + 1);
                        line.remove(currentTokenIndex);
                        line.remove(currentTokenIndex - 1);
                        line.remove(currentTokenIndex - 2);
                    } else {
                        if(line.size() == 4){
                            throw new GargioniException("Variable \"" + before.getValue() + "\" is already defined");
                        }
                        Variable var = table.getVariable((String)before.getValue());
                        table.updateVariable((String) before.getValue(), new Variable(after.getValue(), var.getType(), var.getAccessibility()));
                        line.remove(currentTokenIndex + 1);
                        line.remove(currentTokenIndex);
                        line.remove(currentTokenIndex - 1);
                    }

                    System.out.println("test: " + table.getVariable("test").getValue());
                    break;
                }


                else if (highest.getType().equals(Token.TokenType.PAREN) && highest.getValue().equals(Parenthesis.OPEN)) {
                    // number of opening parenthesis encountered
                    int parenCount = 1;
                    for (int counter = currentTokenIndex + 1; true; counter++) {
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
                    line.remove(currentTokenIndex);
                }

                else if (highest.getType().equals(Token.TokenType.KEYWORD)) {
                    Keyword.evaluate(Keyword.valueOf(((String) highest.getValue()).toUpperCase()), this);
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
