package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;

import java.util.LinkedList;

public class Parser {

    private final String[] statements;
    private int lineNumber;

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
            lineNumber++;
            tokens.add(parseStatement(statement.replaceAll("\n", "")));
        }
    }


    private LinkedList<Token> parseStatement(String statement) throws GargioniException {
        // list of tokens representing the tokenized statement
        LinkedList<Token> line = new LinkedList<>();

        // useful variables when tokenizing a line
        boolean isText = false;
        boolean isString = false;
        boolean isNumber = false;

        int position = 0;

        // the token that is currently being built, to be added to token list when finished building
        Token token = null;

        // here iterate over the string statement and build the token list
        for (char c : statement.toCharArray()) {
            position++;

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
                if (Keyword.isKeyword(String.valueOf(token.getValue()))) {
                    // if it's a keyword, then treat it as such
                    line.add(new Token(Token.TokenType.KEYWORD, token.getValue()));
                } else if(token.getValue().equals("true") || token.getValue().equals("false")){
                    line.add(new Token(Token.TokenType.BOOL, Boolean.parseBoolean((String) token.getValue())));
                } else {
                    // if token is not a keyword, add it as normal text
                    line.add(token);
                }
                // set the current token to null
                token = null;
            }

            else if (isNumber) {
                if (Token.isNumber(c) || c == '.') {
                    token.buildValue(c);
                    continue;
                }

                isNumber = false;

                if (((String) token.getValue()).contains("."))
                    line.add(new Token(Token.TokenType.NUM, Double.parseDouble((String) token.getValue())));
                else
                    line.add(new Token(Token.TokenType.NUM, Integer.parseInt((String) token.getValue())));

                token = null;
            }

            else if (isString) {
                if (c != '"') {
                    token.buildValue(c);
                    continue;
                }

                isString = false;
                line.add(token);
                token = null;
            }


            if (c == '"') {
                isString = true;
                token = new Token(Token.TokenType.STR, "");
                continue;
            }

            if (Token.isNumber(c)) {
                isNumber = true;
                token = new Token(Token.TokenType.NUM, c);
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

            if (c == ' ') {
                continue;
            }

            if((byte) c == 13){
                continue;
            }

            throw new GargioniException("Unable to parse character \"" + c + "\" (" + (byte) c + ") at position " + position + " on line " + lineNumber);
        }

        if (token != null) {
            line.add(token);
        }

        return line;
    }

}
