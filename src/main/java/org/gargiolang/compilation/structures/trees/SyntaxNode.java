package org.gargiolang.compilation.structures.trees;

import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.parser.Operation;
import org.gargiolang.compilation.structures.trees.printer.TreePrinter;
import org.gargiolang.exception.parsing.BadExpressionException;
import org.gargiolang.exception.parsing.ExpectedExpressionException;


/**
 * A SyntaxNode can be viewed as an expression
 */
public class SyntaxNode {

    private SyntaxNode parent;
    private int priority;
    private final Expression type;      // what the expression evaluates to
    private Object value;               // either a SyntaxNode[] or a literal value
    private final Operation operation;

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


    public void parseRequirements() throws ExpectedExpressionException, BadExpressionException {

        priority = 0;

        switch (operation)
        {
            case SUM, SUBTRACTION, MULTIPLICATION, DIVISION, POWER, MODULUS -> {
                binaryCheck(Expression.NUMERIC, Expression.NUMERIC);
                binaryParse();
            }
            case EQUALSTO, NOTEQUALSTO, AND, LESSTHAN, LESSOREQUAL, GREATERTHAN, GREATEROREQUAL, OR -> {
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
        }
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
