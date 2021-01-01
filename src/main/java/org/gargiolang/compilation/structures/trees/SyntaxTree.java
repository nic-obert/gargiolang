package org.gargiolang.compilation.structures.trees;

import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.structures.trees.printer.TreePrinter;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.tokens.TokenLine;

public class SyntaxTree {


    private SyntaxNode root;


    public SyntaxTree() {

    }


    /**
     * Joins the roots of the trees
     * If the this tree's root node is null, it will be set to the other's root node
     * If the other tree's root node is null, do nothing
     *
     * @param other SyntaxTree to join
     */
    public void join(SyntaxTree other) {
        if (root == null) {
            root = other.getRoot();
            return;
        }
        if (other.getRoot() == null)
            return;

        // get to the last root of the tree (this is not a traditional tree with just one root)
        SyntaxNode last = root;
        while (last.getRight() != null) {
            last = last.getRight();
        }

        // append other to the end of linked list
        last.setRight(other.getRoot());
        other.getRoot().setLeft(last);
    }


    public static SyntaxTree fromTokenLine(TokenLine tokenLine) throws TokenConversionException, UnrecognizedTypeException, UndeclaredVariableException {
        return Expression.toSyntaxTree(tokenLine);
    }


    public static void parse(SyntaxNode root) throws ParsingException {
        while (true) {

            // return to the beginning of the line
            while (root.getLeft() != null)
                root = root.getLeft();

            // check if only one root is left
            if (root.getRight() == null)
                break;

            root = SyntaxNode.highestPriority(root);
            root.parseRequirements();
        }

        // traverse back the tree
        while (root.getParent() != null)
            root = root.getParent();
    }


    public void parse() throws ParsingException {
        SyntaxTree.parse(root);
    }


    // setters

    public void setRoot(SyntaxNode root) {
        this.root = root;
    }


    // getters

    public SyntaxNode getRoot() {
        return root;
    }


    public String toString() {
        TreePrinter treePrinter = new TreePrinter();

        for (SyntaxNode node = root; node != null; node = node.getRight()) {
            node.toPrinter(0, treePrinter);
        }

        return treePrinter.toString();
    }
}
