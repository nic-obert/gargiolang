package org.gargiolang.compilation.parser;

public enum Operation {

    // just a literal value, not actually an operation
    LITERAL,

    // arithmetic operations
    SUM,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    POWER,
    INVERSE,        // -a, -1, -sum(4,2)
    MODULUS,
    INCREMENT,
    DECREMENT,

    // logical operations
    NOT,
    AND,
    OR,
    EQUALS_TO,
    NOT_EQUALS_TO,
    GREATER_THAN,
    LESS_THAN,
    LESS_OR_EQUAL,
    GREATER_OR_EQUAL,

    // pointer operations
    DEREFERENCE,    // *a
    ADDRESS_OF,     // &a
    INDEX,          // a[i]

    // assignment operations (maybe add += and -=)
    ASSIGNMENT,

    // declarations
    DECLARATION_INT,
    DECLARATION_BOOL,
    DECLARATION_FLOAT,
    ;

}
