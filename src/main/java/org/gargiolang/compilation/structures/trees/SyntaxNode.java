package org.gargiolang.compilation.structures.trees;

import org.gargiolang.compilation.Compiler;
import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.parser.Operation;
import org.gargiolang.compilation.structures.symboltable.Symbol;
import org.gargiolang.compilation.structures.trees.printer.TreePrinter;
import org.gargiolang.exception.parsing.*;


/**
 * A SyntaxNode can be viewed as an expression
 */
public class SyntaxNode {

    private SyntaxNode parent;
    private int priority;
    private Expression type;      // what the expression evaluates to
    private Object value;         // either a SyntaxNode[] or a literal value (in case of operation == Operation.LITERAL)
    private Operation operation;

    // used when converting from TokenLine to SyntaxTree
    private SyntaxNode left;
    private SyntaxNode right;


    public SyntaxNode(SyntaxNode parent, int priority, Expression type, Object value, Operation operation) {
        this.parent = parent;
        this.priority = priority;
        this.type = type;
        this.value = value;
        this.operation = operation;
    }


    public void parseRequirements() throws ParsingException {

        priority = 0;

        switch (operation)
        {
            case SUM -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                // OPTIMIZATION: if both operands are literal values --> execute the operation right away
                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL
                    && left.getType() != Expression.IDENTIFIER && left.getOperation() == Operation.LITERAL)
                {
                    // differentiate operation based on data types (FLOAT or INTEGER)
                    if (left.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (right.getType()) {
                            case INTEGER -> value = (double) left.getValue() + (int) right.getValue();
                            case FLOAT -> value = (double) left.getValue() + (double) right.getValue();
                        }
                    } else if (right.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (left.getType())
                        {
                            case INTEGER -> value = (int) left.getValue() + (double) right.getValue();
                            case FLOAT -> value = (double) left.getValue() + (double) right.getValue();
                        }
                    } else {
                        type = Expression.INTEGER;
                        value = (int) right.getValue() + (int) left.getValue();
                    }
                }
                // if optimization is not applicable --> proceed as normal
                else {
                    value = new SyntaxNode[]{left, right};
                    // update expression type based on type of operands
                    if (right.getSymbolType() == Expression.FLOAT || left.getSymbolType() == Expression.FLOAT)
                        type = Expression.FLOAT;
                    else
                        type = Expression.INTEGER;
                }

                binaryParse();
            }
            case SUBTRACTION -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                // OPTIMIZATION: if both operands are literal values --> execute the operation right away
                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL
                        && left.getType() != Expression.IDENTIFIER && left.getOperation() == Operation.LITERAL)
                {
                    // differentiate operation based on data types (FLOAT or INTEGER)
                    if (left.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (right.getType()) {
                            case INTEGER -> value = (double) left.getValue() - (int) right.getValue();
                            case FLOAT -> value = (double) left.getValue() - (double) right.getValue();
                        }
                    } else if (right.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (left.getType())
                        {
                            case INTEGER -> value = (int) left.getValue() - (double) right.getValue();
                            case FLOAT -> value = (double) left.getValue() - (double) right.getValue();
                        }
                    } else {
                        type = Expression.INTEGER;
                        value = (int) right.getValue() - (int) left.getValue();
                    }
                }
                // if optimization is not applicable --> proceed as normal
                else {
                    value = new SyntaxNode[]{left, right};
                    // update expression type based on type of operands
                    if (right.getSymbolType() == Expression.FLOAT || left.getSymbolType() == Expression.FLOAT)
                        type = Expression.FLOAT;
                    else
                        type = Expression.INTEGER;
                }

                binaryParse();
            }
            case MULTIPLICATION -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                // OPTIMIZATION: if both operands are literal values --> execute the operation right away
                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL
                        && left.getType() != Expression.IDENTIFIER && left.getOperation() == Operation.LITERAL)
                {
                    // differentiate operation based on data types (FLOAT or INTEGER)
                    if (left.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (right.getType()) {
                            case INTEGER -> value = (double) left.getValue() * (int) right.getValue();
                            case FLOAT -> value = (double) left.getValue() * (double) right.getValue();
                        }
                    } else if (right.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (left.getType())
                        {
                            case INTEGER -> value = (int) left.getValue() * (double) right.getValue();
                            case FLOAT -> value = (double) left.getValue() * (double) right.getValue();
                        }
                    } else {
                        type = Expression.INTEGER;
                        value = (int) right.getValue() * (int) left.getValue();
                    }
                }
                // if optimization is not applicable --> proceed as normal
                else {
                    value = new SyntaxNode[]{left, right};
                    // update expression type based on type of operands
                    if (right.getSymbolType() == Expression.FLOAT || left.getSymbolType() == Expression.FLOAT)
                        type = Expression.FLOAT;
                    else
                        type = Expression.INTEGER;
                }

                binaryParse();
            }
            case DIVISION -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                // OPTIMIZATION: if both operands are literal values --> execute the operation right away
                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL
                        && left.getType() != Expression.IDENTIFIER && left.getOperation() == Operation.LITERAL)
                {
                    // check for division by zero
                    if (right.getValue().equals(0))
                        throw new ZeroDivisionException("Cannot divide by zero");
                    // differentiate operation based on data types (FLOAT or INTEGER)
                    if (left.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (right.getType()) {
                            case INTEGER -> value = (double) left.getValue() / (int) right.getValue();
                            case FLOAT -> value = (double) left.getValue() / (double) right.getValue();
                        }
                    } else if (right.getType() == Expression.FLOAT) {
                        type = Expression.FLOAT;
                        switch (left.getType())
                        {
                            case INTEGER -> value = (int) left.getValue() / (double) right.getValue();
                            case FLOAT -> value = (double) left.getValue() / (double) right.getValue();
                        }
                    } else {
                        type = Expression.INTEGER;
                        value = (int) right.getValue() / (int) left.getValue();
                    }
                }
                // if optimization is not applicable --> proceed as normal
                else {
                    value = new SyntaxNode[]{left, right};
                    // update expression type based on type of operands
                    if (right.getSymbolType() == Expression.FLOAT || left.getSymbolType() == Expression.FLOAT)
                        type = Expression.FLOAT;
                    else
                        type = Expression.INTEGER;
                }

                binaryParse();
            }
            // TODO: 12/12/20 implement power and modulus literal optimizations
            case POWER, MODULUS -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);
                value = new SyntaxNode[]{left, right};
                binaryParse();
            }
            // TODO: 12/12/20 implement logical operator literal optimizations
            case EQUALS_TO, NOT_EQUALS_TO, AND, LESS_THAN, LESS_OR_EQUAL, GREATER_THAN, GREATER_OR_EQUAL, OR -> {
                binaryCheck(Expression.BOOLEAN, Expression.BOOLEAN);
                value = new SyntaxNode[]{left, right};
                binaryParse();
            }
            case INVERSE -> {
                unaryCheck(right, Expression.NUMERIC);

                // OPTIMIZATION: if operand is literal --> execute operation right away
                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL) {
                    operation = Operation.LITERAL;
                    type = right.getType();
                    switch (type)
                    {
                        case INTEGER -> value = (int) right.getValue() * -1;
                        case FLOAT -> value = (double) right.getValue() * -1;
                    }
                }
                // if optimization is not applicable
                else {
                    value = new SyntaxNode[]{right};
                }

                unaryParse(right);
            }
            case NOT -> {
                unaryCheck(right, Expression.BOOLEAN);

                if (right.getType() != Expression.IDENTIFIER && right.getOperation() == Operation.LITERAL) {
                    operation = Operation.LITERAL;
                    value = !((boolean) right.getValue());
                } else {
                    value = new SyntaxNode[]{right};
                }

                unaryParse(right);
            }
            case DECREMENT, INCREMENT -> {
                unaryCheck(left, Expression.NUMERIC);
                value = new SyntaxNode[]{left};
                unaryParse(left);
            }
            case ASSIGNMENT -> {
                binaryCheck(Expression.IDENTIFIER, Expression.EVALUABLE);

                // check the SymbolTable for type of variable to assign value to
                if (left.getSymbolType() != right.getSymbolType())
                    throw new BadTypeException("Cannot assign value of type " + right.getType() + " to variable '" + left + "' of type " + left.getSymbolType());

                value = new SyntaxNode[]{left, right};
                binaryParse();
            }
            case DECLARATION_INT -> declare(Expression.INTEGER);
            case DECLARATION_BOOL -> declare(Expression.BOOLEAN);
            case DECLARATION_FLOAT -> declare(Expression.FLOAT);


        }
    }


    private void declare(Expression symbolType) throws ExpectedExpressionException, BadExpressionException, SymbolRedeclarationException {
        unaryCheck(right, Expression.IDENTIFIER);

        // declare symbol in the SymbolTable
        Compiler.symbolTable().declare((String) right.getValue(), new Symbol(symbolType));

        // transform into an identifier
        operation = Operation.LITERAL;
        value = right.getValue(); // value becomes the reference to the declared variable

        // remove SyntaxNode from linked list
        right = right.getRight();
        if (right != null)
            right.setLeft(this);
    }


    private void unaryCheck(SyntaxNode node, Expression required) throws ExpectedExpressionException, BadExpressionException {
        if (node == null)
            if (right == null)
                throw new ExpectedExpressionException(operation + " operation requires an expression on the right, but none was found");
            else
                throw new ExpectedExpressionException(operation + " operation requires an expression on the left, but none was found");


        if (!node.getType().is(required))
            if (node == left)
                throw new BadExpressionException(operation + " operation requires a " + required + " expression on the left, but " + left.getType() + " was found");
            else
                throw new BadExpressionException(operation + " operation requires a " + required + " expression on the right, but " + right.getType() + " was found");


    }

    private void binaryCheck(Expression requiredLeft, Expression requiredRight) throws ExpectedExpressionException, BadExpressionException {
        if (right == null)
            throw new ExpectedExpressionException(operation + " operation requires an expression on the right, but none was found");
        if (left == null)
            throw new ExpectedExpressionException(operation + " operation requires an expression on the left, but none was found");

        if (!right.getType().is(Expression.EVALUABLE))
            throw new BadExpressionException(operation + " operation requires a " + requiredRight + " expression on the right, but " + right.getType() + " was found");
        if (!left.getType().is(Expression.EVALUABLE))
            throw new BadExpressionException(operation + " operation requires a " + requiredLeft + " expression on the left, but " + left.getType() + " was found");

    }


    private void unaryParse(SyntaxNode node) {

        node.setParent(this);

        if (node == right) {
            right = right.getRight();
            if (right != null)
                right.setLeft(this);
        } else {
            left = left.getLeft();
            if (left != null)
                left.setRight(this);
        }
    }

    private void binaryParse() {

        left.setParent(this);
        right.setParent(this);

        left = left.getLeft();
        if (left != null)
            left.setRight(this);

        right = right.getRight();
        if (right != null)
            right.setLeft(this);
    }


    // getters

    public SyntaxNode getParent() {
        return parent;
    }

    public Expression getType() {
        return type;
    }

    public Expression getSymbolType() throws UndeclaredSymbolException {
        if (type == Expression.IDENTIFIER)
            return Compiler.symbolTable().getSymbol((String) value).type;
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public Object getValue() {
        return value;
    }

    public Operation getOperation() {
        return operation;
    }

    public SyntaxNode getLeft() {
        return left;
    }

    public SyntaxNode getRight() {
        return right;
    }


    // setters

    public void setRight(SyntaxNode right) {
        this.right = right;
    }

    public void setLeft(SyntaxNode left) {
        this.left = left;
    }

    public void setParent(SyntaxNode parent) {
        this.parent = parent;
    }


    public void toPrinter(int depth, TreePrinter treePrinter) {
        treePrinter.enqueue(depth, this);
        depth ++;

        if (operation != Operation.LITERAL && value != null) {
            for (SyntaxNode node : (SyntaxNode[]) value) {
                node.toPrinter(depth, treePrinter);
            }
        }
    }


    @Override
    public String toString() {
        if (operation == Operation.LITERAL)
            return value.toString();

        return "<" + type + ": " + operation + ">";
    }


    public static SyntaxNode highestPriority(SyntaxNode nodeList) {
        SyntaxNode highest = nodeList;
        for ( ; nodeList != null; nodeList = nodeList.getRight()) {
            if (nodeList.getPriority() > highest.getPriority()) {
                highest = nodeList;
            }
        }
        return highest;
    }
}
