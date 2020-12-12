package org.gargiolang.compilation.parser;

public enum Operation {

    LITERAL,

    SUM,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    POWER,
    INVERSE,
    MODULUS,
    INCREMENT,
    DECREMENT,

    NOT,
    AND,
    OR,
    EQUALS_TO,
    NOT_EQUALS_TO,
    GREATER_THAN,
    LESS_THAN,
    LESS_OR_EQUAL,
    GREATER_OR_EQUAL,

    DEREFERENCE,    // *a
    ADDRESS_OF,     // &a

    ASSIGNMENT,

    // declarations
    DECLARATION_INT,
    DECLARATION_BOOL,
    DECLARATION_FLOAT,
    ;

}
