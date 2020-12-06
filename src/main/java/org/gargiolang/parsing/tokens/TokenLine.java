package org.gargiolang.parsing.tokens;

import java.util.function.Consumer;

/**
 * A doubly linked list optimized for dealing with tokens
 */
public class TokenLine {

    private Token firstToken;
    private Token lastToken;

    public TokenLine() {

    }


    public void append(Token token) {
        if (lastToken != null) {
            lastToken.setNext(token);
            token.setPrev(lastToken);
            lastToken = token;
            return;
        }
        // means line is empty
        firstToken = token;
        lastToken = token;
    }


    public void insertBefore(Token before, Token token) {
        token.setNext(before);
        if (before.hasPrev()) {
            before.getPrev().setNext(token);
            token.setPrev(before.getPrev());
            before.setPrev(token);
            return;
        }
        // means before was the first token of the line
        firstToken = token;
        before.setPrev(token);
    }

    public void insertAfter(Token after, Token token) {
        token.setPrev(after);
        if (after.hasNext()) {
            after.getNext().setPrev(token);
            token.setNext(after.getNext());
            after.setNext(token);
            return;
        }
        // means after was the last token of the line
        lastToken = token;
        after.setNext(token);
    }


    public void insert(int index, Token token) {
        if (index == 0) {
            insertFirst(token);
            return;
        }
        Token old = get(index);
        if (old == lastToken) {
            append(token);
            return;
        }
        // means the index is somewhere in the middle (and has both a previous and a next)
        token.setPrev(old.getPrev());
        token.setNext(old);
        old.setPrev(token);
        token.getPrev().setNext(token);
    }

    public void insertFirst(Token token) {
        if (firstToken != null) {
            firstToken.setPrev(token);
            firstToken = token;
            return;
        }
        // means the line is empty
        firstToken = token;
        lastToken = token;
    }


    /**
     * Removes the specified token from the line.
     * Does not check if the token is actually in the line.
     *
     * @param token the token to be removed from the line
     */
    public void remove(Token token) {
        // check if token is the first
        if (token.is(firstToken)) {
            // if token is both the first and the last --> it's the only token in the line
            if (token.is(lastToken)) {
                clear();
                return;
            }

            // token is the first, but not the last
            firstToken = token.getNext();
            firstToken.setPrev(null);
            return;
        }
        // if token is the last, but not the first
        if (token.is(lastToken)) {
            lastToken = token.getPrev();
            lastToken.setNext(null);
            return;
        }

        // token is neither the first, nor the last
        token.getPrev().setNext(token.getNext());
        token.getNext().setPrev(token.getPrev());
    }


    public void removeFirst() {
        if (firstToken.hasNext()) {
            firstToken = firstToken.getNext();
        }
        // line has only one token
        clear();
    }

    public void removeLast() {
        if (lastToken.hasNext()) {
            lastToken = lastToken.getNext();
        }
        // line has only one token
        clear();
    }


    public void removeFrom(Token from) {
        if (from.hasPrev()) {
            lastToken = from.getPrev();
            return;
        }
        clear();
    }


    public void removeUntil(Token until) {
        if (until.hasNext()) {
            firstToken = until.getNext();
            return;
        }
        clear();
    }


    public void clear() {
        firstToken = null;
        lastToken = null;
    }


    public Token get(int index) {
        Token token = firstToken;
        for (; index != 0; index--) {
            token = token.getNext();
        }
        return token;
    }


    public void set(int index, Token token) {
        Token old = get(index);

        if (old.hasPrev()) {
            old.getPrev().setNext(token);
            token.setPrev(old.getPrev());

            if (old.hasNext()) {
                old.getNext().setPrev(token);
                token.setNext(old.getNext());
                return;
            }

            // token has no next --> is the last of the line
            lastToken = token;
            return;
        }

        // token has no prev --> is the first of the line
        if (old.hasNext()) {
            old.getNext().setPrev(token);
            token.setNext(old.getNext());
            firstToken = token;
            return;
        }

        // token has neither prev nor next --> is the only token in the line
        firstToken = token;
        lastToken = token;
    }


    /**
     * Replaces the specified token with the given one.
     * Does not check if token is actually in the line
     *
     * @param old the old token to replace
     * @param token the new replacement token
     */
    public void replace(Token old, Token token) {
        token.setPrev(old.getPrev());
        token.setNext(old.getNext());
        if (token.hasPrev()) {
            token.getPrev().setNext(token);
        } else {
            firstToken = token;
        }
        if (token.hasNext()) {
            token.getNext().setPrev(token);
        } else {
            lastToken = token;
        }
    }


    public void forEach(Consumer<Token> function) {
        for (Token token = firstToken; token != lastToken.getNext(); token = token.getNext()) {
            function.accept(token);
        }
    }


    /**
     * Return a sublist of the line starting from firstToken
     * up until lastToken included.
     *
     * @param firstToken first token of the line
     * @param lastToken last token of the line
     * @return line starting from firstToken up until lastToken included
     */
    public TokenLine subList(Token firstToken, Token lastToken) {
        TokenLine tokenLine = new TokenLine();

        tokenLine.setFirstToken(firstToken);
        tokenLine.setLastToken(lastToken);

        return tokenLine;
    }


    /**
     * Returns the index of the given token in the line.
     * Returns -1 if token is not in the list.
     *
     * @param token token to get the index of
     * @return the index of the token or -1 if token is not in the list
     */
    public int indexOf(Token token) {
        int index = 0;
        for (Token t = firstToken; !t.is(lastToken.getNext()); t = t.getNext()) {
            if (t.is(token)) return index;
            index ++;
        }
        // if token is not found --> return -1
        return -1;
    }


    public Token highestPriority() {
        Token highest = firstToken;

        // if the first token of the line is a scope --> evaluate it right away
        if (highest.getType() == TokenType.SCOPE)
            return highest;

        for (Token token = firstToken; token != lastToken.getNext(); token = token.getNext()) {
            if (token.getPriority() > highest.getPriority()) {
                highest = token;

                // line evaluation should not go beyond the current scope
                if (token.getType().equals(TokenType.SCOPE))
                    break;
            }
        }

        return highest;
    }


    public boolean isEmpty() {
        return firstToken == null;
    }


    /**
     * Returns the number of Tokens in the line.
     * NOTE: to check if line is empty use the .isEmpty() method instead
     *
     * @return the number of tokens in the line.
     */
    public int size() {
        if (this.isEmpty())
            return 0;

        Token until = lastToken.getNext();
        int i = 0;
        for (Token token = firstToken; token != until; token = token.getNext()) {
            i ++;
        }
        return i;
    }

    public Token getFirst() {
        return firstToken;
    }

    public Token getLast() {
        return lastToken;
    }

    public TokenLine copy() {
        TokenLine tokenLine = new TokenLine();

        if (this.isEmpty())
            return tokenLine;

        Token until = lastToken.getNext();
        for (Token token = firstToken; token != until; token = token.getNext()) {
            tokenLine.append(token.copy());
        }
        return tokenLine;
    }

    @Override
    public String toString() {
        if (this.isEmpty())
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        forEach(token -> stringBuilder.append(token).append(" "));
        return stringBuilder.toString();
    }

    private void setFirstToken(Token token) {
        this.firstToken = token;
    }

    private void setLastToken(Token token) {
        this.lastToken = token;
    }

}
