package org.gargiolang.exception.parsing;

public class UnexpectedTokenException extends ParsingException {
    public UnexpectedTokenException(String exception) {
        super(exception);
    }
}