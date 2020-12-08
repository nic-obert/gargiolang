package org.gargiolang.compilation.structures;

import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.parser.Operation;
import org.gargiolang.exception.parsing.RequirementsNotFoundException;


/**
 * A SyntaxNode can be viewed as an expression
 */
public class SyntaxNode {

    private final SyntaxNode parent;
    private final int priority;
    private final Expression type; // what the expression evaluates to
    private final Object value;
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

    public String toString() {
        // TODO: 08/12/20 to implement
        return "to implement";
    }
}
