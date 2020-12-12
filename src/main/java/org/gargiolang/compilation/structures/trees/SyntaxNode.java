package org.gargiolang.compilation.structures.trees;

import org.gargiolang.compilation.Compiler;
import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.parser.Operation;
import org.gargiolang.compilation.structures.symboltable.Symbol;
import org.gargiolang.compilation.structures.trees.printer.TreePrinter;
import org.gargiolang.exception.parsing.BadExpressionException;
import org.gargiolang.exception.parsing.BadTypeException;
import org.gargiolang.exception.parsing.ExpectedExpressionException;
import org.gargiolang.exception.parsing.ParsingException;


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
            case SUM, SUBTRACTION, MULTIPLICATION -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                // update expression type based on type of operands
                if (right.getType() == Expression.FLOAT || left.getType() == Expression.FLOAT)
                    type = Expression.FLOAT;
                else
                    type = Expression.INTEGER;

                binaryParse();
            }
            case DIVISION -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);

                if (right.getType() == Expression.INTEGER && left.getType() == Expression.INTEGER)
                    type = Expression.INTEGER;
                else
                    type = Expression.FLOAT;

                binaryParse();
            }
            case POWER, MODULUS -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);
                binaryParse();
            }
            case EQUALS_TO, NOT_EQUALS_TO, AND, LESS_THAN, LESS_OR_EQUAL, GREATER_THAN, GREATER_OR_EQUAL, OR -> {
                binaryCheck(Expression.BOOLEAN, Expression.BOOLEAN);
                binaryParse();
            }
            case INVERSE -> {
                unaryCheck(right, Expression.NUMERIC);
                unaryParse(right);
            }
            case NOT -> {
                unaryCheck(right, Expression.BOOLEAN);
                unaryParse(right);
            }
            case DECREMENT, INCREMENT -> {
                unaryCheck(left, Expression.NUMERIC);
                unaryParse(left);
            }
            case ASSIGNMENT -> {
                binaryCheck(Expression.IDENTIFIER, Expression.EVALUABLE);

                // check the SymbolTable for type of variable to assign value to
                // assuming left.getValue() returns a string because of binaryCheck()
                Expression varType = Compiler.symbolTable().getSymbol((String) left.getValue()).type;
                if (varType != right.getType())
                    throw new BadTypeException("Cannot assign value of type " + right.getType() + " to variable '" + left + "' of type " + varType);

                binaryParse();
            }
            case DECLARATION_INT -> declare(Expression.INTEGER);
            case DECLARATION_BOOL -> declare(Expression.BOOLEAN);
            case DECLARATION_FLOAT -> declare(Expression.FLOAT);


        }
    }


    private void declare(Expression symbolType) throws ExpectedExpressionException, BadExpressionException {
        unaryCheck(right, Expression.IDENTIFIER);

        // declare symbol in the SymbolTable
        Compiler.symbolTable().declare((String) right.getValue(), new Symbol(symbolType));

        // transform into an identifier
        operation = Operation.LITERAL;
        value = right.getValue(); // value becomes the reference to the declared variable

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
        value = new SyntaxNode[]{node};

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
        value = new SyntaxNode[]{left, right};

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
