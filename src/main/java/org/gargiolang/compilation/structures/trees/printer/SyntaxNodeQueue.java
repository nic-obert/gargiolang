package org.gargiolang.compilation.structures.trees.printer;

import org.gargiolang.compilation.structures.trees.SyntaxNode;
import org.gargiolang.compilation.structures.trees.printer.Node;

/**
 * A basic implementation of a Queue data structure
 */
public class SyntaxNodeQueue {

    private Node first;
    private Node last;

    public SyntaxNodeQueue() {

    }

    public void enqueue(SyntaxNode syntaxNode) {
        Node newNode = new Node(syntaxNode);

        if (last != null)
            last.setNext(newNode);
        else if (first == null)
            first = newNode;

        last = newNode;
    }

    public SyntaxNode dequeue() {
        if (first == null)
            return null;

        SyntaxNode syntaxNode = first.getNode();
        first = first.getNext();

        return syntaxNode;
    }

}
