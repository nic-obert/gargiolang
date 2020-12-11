package org.gargiolang.compilation.structures.trees.printer;

import org.gargiolang.compilation.structures.trees.SyntaxNode;

public class Node {

    private Node next;
    private final SyntaxNode node;

    public Node(SyntaxNode node) {
        this.node = node;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public SyntaxNode getNode() {
        return node;
    }
}
