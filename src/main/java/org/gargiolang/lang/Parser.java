package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;

import java.util.LinkedList;

public class Parser {

    private final String[] statements;

    // a 2d linked list of lines of tokens
    private final LinkedList<LinkedList<Token>> tokens;

    public Parser(String[] statements) {
        this.statements = statements;
        this.tokens = new LinkedList<>();
    }


    public LinkedList<LinkedList<Token>> getTokens() {
        return tokens;
    }


    public void parseTokens() throws GargioniException {
        for(String statement : statements) {
            tokens.add(parseStatement(statement));
        }
    }


    private LinkedList<Token> parseStatement(String statement) throws GargioniException {
        // list of tokens representing the tokenized statement
        LinkedList<Token> line = new LinkedList<>();

        // useful variables when tokenizing a line
        boolean isText = false;
        boolean isString = false;

        // the token that is currently being built, to be added to token list when finished building
        Token token = null;

        // here iterate over the string statement and build the token list
        for (char c : statement.toCharArray()) {

            // if the parser is already parsing a token of type text
            if (isText) {
                // check if char can be a token of type text
                if (Token.isText(c) || Token.isNumber(c)) {
                    token.buildValue(c);
                    continue;
                }
                // if char is not text --> it has finished building, thus it should be appended to the token list
                isText = false;
                // firstly check if the token is a keyword
                if (Keyword.isKeyword((String) token.getValue())) {
                    // if it's a keyword, then treat it as such
                    line.add(new Token(Token.TokenType.KEYWORD, token.getValue()));
                } else {
                    // if token is not a keyword, add it as normal text
                    line.add(token);
                }
                // set the current token to null
                token = null;
            }

            if (Token.isNumber(c) || (c == '.' && token != null && token.getType() == Token.TokenType.NUM)) {
                if (token == null) {
                    token = new Token(Token.TokenType.NUM, c-48); // c-48 --> see the ASCII table
                } else if (c == '.') {
                    token.buildValue('.');
                } else {
                    token.buildValue((char) (c-48));
                }
                continue;
            }

            if (Token.isText(c)) {
                isText = true;
                token = new Token(Token.TokenType.TXT, c);
                continue;
            }

            if (Token.isArithmeticOperator(c)) {
                token = new Token(Token.TokenType.ARITHMETIC_OPERATOR, c);
                line.add(token);
                token = null;
                continue;
            }


            throw new GargioniException("Unable to parse character \"" + c + "\"");
        }

        if (token != null) {
            line.add(token);
        }

        return line;
    }
}
