package org.gargiolang.parsing.tokens.operators;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.parsing.tokens.Token;
import org.gargiolang.parsing.tokens.TokenLine;
import org.gargiolang.parsing.tokens.TokenType;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Accessibility;
import org.gargiolang.runtime.variable.SymbolTable;
import org.gargiolang.runtime.variable.Variable;

public class AssignmentOperator {

    public static void evaluate(Interpreter interpreter) throws NotLValueException, VariableRedeclarationException, UnrecognizedTypeException, UndeclaredVariableException, BadTypeException {
        Runtime runtime = Runtime.getRuntime();
        SymbolTable table = runtime.getSymbolTable();
        Token operator = interpreter.getCurrentToken();
        TokenLine line = interpreter.getLine();

        Token lValue = operator.getPrev();
        Token rValue = operator.getNext();

        // remove tokens from line
        line.remove(operator);
        line.remove(rValue);
        line.remove(lValue);

        // check if lValue is actually an lvalue
        if (lValue.getType() != TokenType.TXT)
            throw new NotLValueException(lValue + " is not an lvalue");

        // check if a variable type is specified --> this is a variable declaration and it should be added to the SymbolTable
        if (lValue.hasPrev()) {
            Token type = lValue.getPrev();

            // check if the type is actually a type
            if (type.getType() == TokenType.TYPE) {

                // remove token from line
                line.remove(type);

                Variable variable = new Variable(
                        rValue.getVarValue(runtime),
                        Variable.Type.valueOf(type.getValue().toString()),
                        Accessibility.PUBLIC);
                table.addVariable((String) lValue.getValue(), variable);

                return;
            }
        }

        // whereas, if a variable type is not specified, the variable should already have been declared --> update the SymbolTable
        Variable variable = new Variable(
                rValue.getVarValue(runtime),
                rValue.getVarType(runtime),
                Accessibility.PUBLIC);
        table.updateVariable((String) lValue.getValue(), variable);

    }

}
