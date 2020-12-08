package org.gargiolang.compilation.structures;

import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.tokens.TokenLine;

public class SyntaxTree {


    private SyntaxNode root;


    public SyntaxTree() {

    }


    public static SyntaxTree fromTokenLine(TokenLine tokenLine) throws TokenConversionException {
        return Expression.toSyntaxTree(tokenLine);
    }


    public void parse() {

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
        // TODO: 08/12/20 to implement
        return "to implement";
    }
}
