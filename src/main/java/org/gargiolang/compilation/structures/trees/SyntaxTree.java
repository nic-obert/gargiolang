package org.gargiolang.compilation.structures.trees;

import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.structures.trees.printer.TreePrinter;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.BadExpressionException;
import org.gargiolang.exception.parsing.ExpectedExpressionException;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.tokens.TokenLine;

public class SyntaxTree {


    private SyntaxNode root;


    public SyntaxTree() {

    }


    public static SyntaxTree fromTokenLine(TokenLine tokenLine) throws TokenConversionException, UnrecognizedTypeException, UndeclaredVariableException {
        return Expression.toSyntaxTree(tokenLine);
    }


    public void parse() throws ParsingException {
        SyntaxNode highest;
        while (true) {

            // return to the beginning of the line
            while (root.getLeft() != null)
                root = root.getLeft();

            // check if only one root is left
            if (root.getRight() == null)
                break;

            highest = SyntaxNode.highestPriority(root);

            root = highest;
            root.parseRequirements();
        }

        // traverse back the tree
        while (root.getParent() != null)
            root = root.getParent();
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
