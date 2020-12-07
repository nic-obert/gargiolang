package org.gargiolang.tokenizer.tokens;

import org.gargiolang.exception.evaluation.BadTypeException;
import org.gargiolang.runtime.variable.Variable;

public enum TokenType {

    TXT((byte) 0),
    STR((byte) 0),
    NUM((byte) 0),
    BOOL((byte) 0),
    NULL((byte) 0),

    TYPE((byte) 0),

    SCOPE((byte) 1),

    ASSIGNMENT_OPERATOR((byte) 1),

    FUNC((byte) 2),

    PAREN((byte) 10), // highest priority

    LOGICAL_OPERATOR((byte) -1), // priority depends on the operator
    ARITHMETIC_OPERATOR((byte) -1), // priority depends on the operator
    CALL((byte) -1), // priority depends on whether it is open or closed
    KEYWORD((byte) -1); // priority depends on the keyword



    private final byte priority;
    TokenType(byte i) {
        this.priority = i;
    }

    public int getPriority() {
        return priority;
    }


    /**
     * Converts a variable type to a token type
     *
     * @param type the variable type
     * @return the matching token type
     * @throws BadTypeException if the provided type has no conversion
     */
    public static TokenType fromVarType(Variable.Type type) throws BadTypeException {
        switch (type)
        {
            case STRING: return STR;
            case INT, FLOAT: return NUM;
            case BOOLEAN: return BOOL;
        }

        throw new BadTypeException("No conversion from variable type '" + type + "' to Token type");
    }

}
