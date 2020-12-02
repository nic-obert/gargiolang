package org.gargiolang.parsing.tokens;

/**
 * A doubly linked list optimized for dealing with tokens
 */
public class TokenLine {

    private int size = 0;
    private Token currentToken;
    private Token firstToken;
    private Token lastToken;

    public TokenLine() {

    }

    public void addToken(Token token) {
        this.size ++;

    }

}
