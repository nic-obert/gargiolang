package org.gargiolang.parsing;

import org.gargiolang.exception.parsing.InvalidCharacterException;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.exception.parsing.UnexpectedTokenException;
import org.gargiolang.parsing.tokens.operators.ArithmeticOperator;
import org.gargiolang.parsing.tokens.operators.LogicalOperator;
import org.gargiolang.parsing.tokens.operators.Parenthesis;
import org.gargiolang.parsing.tokens.Call;
import org.gargiolang.parsing.tokens.Keyword;
import org.gargiolang.parsing.tokens.Scope;
import org.gargiolang.parsing.tokens.Token;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class Parser {

    private final Runtime runtime;

    private final LinkedList<String> statements;
    private int lineNumber;

    // a 2d linked list of lines of tokens
    private final LinkedList<LinkedList<Token>> tokens;

    public Parser(LinkedList<String> statements, Runtime runtime) {
        this.statements = statements;
        this.tokens = new LinkedList<>();
        this.runtime = runtime;
    }


    public LinkedList<LinkedList<Token>> getTokens() {
        return tokens;
    }


    public LinkedList<LinkedList<Token>> parseTokens() throws ParsingException {
        for(String statement : statements) {
            lineNumber++;
            if (statement == null) continue;

            // check if line is labelled
            if (statement.stripLeading().startsWith("@")) {
                String label = statement.substring(statement.indexOf('@')+1).strip();
                runtime.getLabelTable().putLabel(label, statements.indexOf(statement));
                // do not parse labelled line
                continue;
            }

            LinkedList<Token> line = parseStatement(statement + (char) 0);
            // ignore empty statements
            if (line.size() != 0)
                tokens.add(line);
        }

        return tokens;
    }


    private LinkedList<Token> parseStatement(String statement) throws ParsingException {
        // list of tokens representing the tokenized statement
        LinkedList<Token> line = new LinkedList<>();

        // the current state of the tokenizer
        State state = State.NULL;

        // for function calls inside other function calls
        int parenCount = 0;
        int callDepth = 0;

        // escape (backslash) for strings
        boolean escape = false;

        // for exception verbosity
        int position = 0;

        // the token that is currently being built, to be added to token list when finished building
        Token token = null;

        // here iterate over the string statement and build the token list
        for (char c : statement.toCharArray()) {
            position ++;


            switch (state)
            {
                // if the parser is already parsing a token of type text
                case TEXT -> {
                    // check if char can be a token of type text
                    if (Token.isText(c) || Token.isNumber(c)) {
                        token.buildValue(c);
                        continue;
                    }
                    // if char is not text --> it has finished building, thus it should be appended to the token list
                    state = State.NULL;

                    // firstly check if the token is a Keyword
                    if (Keyword.isKeyword((String) token.getValue())) {
                        line.add(new Token(Token.TokenType.KEYWORD, Keyword.valueOf(((String) token.getValue()).toUpperCase())));
                    }

                    // check if token is a Type
                    else if(Variable.Type.getType(String.valueOf(token.getValue())) != null) {
                        line.add(new Token(Token.TokenType.TYPE, Variable.Type.getType(String.valueOf(token.getValue()))));
                    }

                    // check if is a Boolean
                    else if(token.getValue().equals("true")) {
                        line.add(new Token(Token.TokenType.BOOL, true));
                    }

                    else if (token.getValue().equals("false")) {
                        line.add(new Token(Token.TokenType.BOOL, false));
                    }

                    // check if it is a function call
                    else if (c == '(') {
                        // keywords are not functions
                        if (!(line.size() != 0 && line.getLast().getType().equals(Token.TokenType.KEYWORD))) {
                            // add the text as function
                            line.add(new Token(Token.TokenType.FUNC, token.getValue()));
                            // increase the call depth (for function calls inside other calls)
                            callDepth ++;
                            // add an opening Call parenthesis
                            line.add(new Token(Token.TokenType.CALL, Call.OPEN));
                            token = null;
                            continue;
                        }
                    }

                    // if token is not a keyword, add it as normal text
                    else {
                        line.add(token);
                    }

                    // set the current token to null
                    token = null;
                }

                case NUMBER -> {
                    if (Token.isNumber(c) || c == '.' || c == '-') {
                        token.buildValue(c);
                        continue;
                    }

                    state = State.NULL;

                    if (((String) token.getValue()).contains("."))
                        line.add(new Token(Token.TokenType.NUM, Double.parseDouble((String) token.getValue())));
                    else
                        line.add(new Token(Token.TokenType.NUM, Integer.parseInt((String) token.getValue())));

                    token = null;
                }

                case STRING -> {

                    // check for \ escapes
                    if (c == '\\') {
                        escape = !escape;
                        continue;
                    }

                    if (escape) {
                        switch (c) {
                            case 'n' -> c = '\n';
                            case 't' -> c = '\t';
                            case 'r' -> c = '\r';
                            case '"' -> {
                                token.buildValue('"');
                                escape = false;
                                continue;
                            }
                        }
                        escape = false;
                    }
                    if (c != '"') {
                        token.buildValue(c);
                        continue;
                    }

                    // if this line is reached it means that c == '"' and it has not been escaped
                    state = State.NULL;
                    line.add(token);
                    token = null;
                    continue;
                }

                case COMMENT -> {
                    if (c == '\n')
                        state = State.NULL;
                    continue;
                }
            }


            // checks for function calls
            if (callDepth != 0) {
                if (c == '(') parenCount ++;
                else if (c == ')') {
                    parenCount --;
                    // when the closing parenthesis is met
                    if (parenCount == -1) {

                        callDepth --;

                        line.add(new Token(Token.TokenType.CALL, Call.CLOSE));
                        continue;
                    }
                }
            }


            if (c == '"') {
                if (token != null) line.add(token);
                state = State.STRING;
                token = new Token(Token.TokenType.STR, "");
                continue;
            }

            if (Token.isNumber(c)) {
                if (token != null) line.add(token);
                state = State.NUMBER;
                token = new Token(Token.TokenType.NUM, Character.toString(c));
                continue;
            }

            if(c == '-'){
                if (token != null) line.add(token);
                state = State.NUMBER;
                token = new Token(Token.TokenType.NUM, Character.toString(c));
                continue;
            }

            if (Token.isText(c)) {
                if (token != null) line.add(token);
                state = State.TEXT;
                token = new Token(Token.TokenType.TXT, Character.toString(c));
                continue;
            }

            if (Token.isArithmeticOperator(c)) {
                if (token != null) {

                    // check for arithmetic operators composed by multiple characters (++, --) and comments (//)
                    if (token.getType().equals(Token.TokenType.ARITHMETIC_OPERATOR)) {

                        if (token.getValue().equals(ArithmeticOperator.ADD) && c == '+') {
                            line.add(new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.INC));
                        }
                        else if (token.getValue().equals(ArithmeticOperator.SUB) && c == '-') {
                            line.add(new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.DEC));
                        }
                        else if(token.getValue().equals(ArithmeticOperator.MUL) && c == '*'){
                            line.add(new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.POW));
                        }
                        // in case of a comment --> let it be handle by the above switch statement
                        else if (token.getValue().equals(ArithmeticOperator.DIV) && c == '/') {
                            state = State.COMMENT;
                        }

                        token = null;
                        continue;
                    }
                    else
                        line.add(token);
                }
                token = new Token(Token.TokenType.ARITHMETIC_OPERATOR, ArithmeticOperator.fromString(Character.toString(c)));
                continue;
            }

            if(Token.isLogicalOperator(c)){
                if(token != null) {

                    if (token.getType().equals(Token.TokenType.LOGICAL_OPERATOR)) {
                        if (token.getValue().equals(LogicalOperator.INCOMPLETE_AND) && c == '&') token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.AND);
                        else if (token.getValue().equals(LogicalOperator.INCOMPLETE_OR) && c == '|') token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.OR);

                        line.add(token);
                        token = null;
                        continue;
                    }

                    line.add(token);
                }
                token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.fromString(Character.toString(c)));
                continue;
            }

            if (c == '=') {
                if (token != null) {

                    // means the token is "=="
                    if (token.getType().equals(Token.TokenType.ASSIGNMENT_OPERATOR)) {
                        token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.EQ);
                        line.add(token);
                        token = null;
                        continue;
                    }
                    // <= >= !=
                    else if (token.getType().equals(Token.TokenType.LOGICAL_OPERATOR)) {
                        switch ((LogicalOperator) token.getValue())
                        {
                            case GR -> token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.GRE);
                            case LS -> token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.LSE);
                            case NOT -> token = new Token(Token.TokenType.LOGICAL_OPERATOR, LogicalOperator.NE);
                            default -> throw new UnexpectedTokenException("Unexpected token: '=' after " + token);
                        }

                        line.add(token);
                        token = null;
                        continue;
                    }

                    line.add(token);
                }

                token = new Token(Token.TokenType.ASSIGNMENT_OPERATOR, '=');
                continue;
            }

            if (c == ' ' || c == '\n') {
                if (token != null) line.add(token);
                token = null;
                continue;
            }

            if (c == '(') {
                if (token != null) line.add(token);
                token = new Token(Token.TokenType.PAREN, Parenthesis.OPEN);
                continue;
            }

            if (c == ')') {
                if (token != null) line.add(token);
                token = new Token(Token.TokenType.PAREN, Parenthesis.CLOSED);
                continue;
            }

            if (c == ',') {
                if (token != null) line.add(token);
                continue;
            }

            // ASCII 13 --> carriage return
            if ((byte) c == 13){
                continue;
            }

            if (c == '{') {
                if (token != null) line.add(token);
                token = new Token(Token.TokenType.SCOPE, Scope.OPEN);
                continue;
            }

            if (c == '}') {
                if (token != null) line.add(token);
                token = new Token(Token.TokenType.SCOPE, Scope.CLOSE);
                continue;
            }

            if(c == '#') continue;

            // null termination character
            if (c == 0) break;

            if (c == ';') break;


            throw new InvalidCharacterException("Unable to parse character \"" + c + "\" (" + (byte)c + ") at position " + position + " on line " + lineNumber);
        }

        if (token != null) {
            line.add(token);
        }

        return line;
    }


    public enum State {

        NULL,
        TEXT,
        NUMBER,
        STRING,
        COMMENT

    }

}
