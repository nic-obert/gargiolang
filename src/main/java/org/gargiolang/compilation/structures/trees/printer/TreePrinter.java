package org.gargiolang.compilation.structures.trees.printer;

import org.gargiolang.compilation.structures.trees.SyntaxNode;
import org.gargiolang.compilation.structures.trees.printer.SyntaxNodeQueue;

import java.util.LinkedList;

public class TreePrinter {

    private final LinkedList<SyntaxNodeQueue> tree;

    public TreePrinter() {
        this.tree = new LinkedList<>();
    }

    private void addLayer() {
        tree.add(new SyntaxNodeQueue());
    }

    public void enqueue(int depth, SyntaxNode node) {
        if (tree.size() == depth)
            addLayer();

        tree.get(depth).enqueue(node);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SyntaxNodeQueue queue : tree) {
            for (SyntaxNode node = queue.dequeue(); node != null; node = queue.dequeue()) {
                stringBuilder.append(node).append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}
