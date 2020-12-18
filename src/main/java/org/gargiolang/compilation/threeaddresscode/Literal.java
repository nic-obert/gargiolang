package org.gargiolang.compilation.threeaddresscode;

import org.gargiolang.compilation.structures.trees.SyntaxNode;

public class Literal extends Value {

    public static Literal zero = new Literal(0);
    public static Literal one = new Literal(1);

    public final int value;

    public Literal(int value) {
        this.value = value;
    }

    public static Literal fromSyntaxNode(SyntaxNode node, int index) {
        return new Literal((int) ((SyntaxNode[]) node.getValue())[index].getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
