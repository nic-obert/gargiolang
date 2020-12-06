package org.gargiolang.parsing.tokens;

public class TokenBlock {

    private final int firstLine;
    private final Token firstToken;
    private final int lastLine;
    private final Token lastToken;

    public TokenBlock(int firstLine, Token firstToken, int lastLine, Token lastToken) {
        this.firstLine = firstLine;
        this.firstToken = firstToken;
        this.lastLine = lastLine;
        this.lastToken = lastToken;
    }

    public int getFirstLine() {
        return firstLine;
    }

    public int getLastLine() {
        return lastLine;
    }

    public Token getFirstToken() {
        return firstToken;
    }

    public Token getLastToken() {
        return lastToken;
    }

}
