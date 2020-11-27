package org.gargiolang.lang.operators;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.lang.Token;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.*;
import org.gargiolang.runtime.variable.Accessibility;
import org.gargiolang.runtime.variable.SymbolTable;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class AssignmentOperator {

    public static void evaluate(Interpreter interpreter) throws NotLValueException, VariableRedeclarationException, UnrecognizedTypeException, UndeclaredVariableException, BadTypeException {
        Runtime runtime = Runtime.getRuntime();
        SymbolTable table = runtime.getSymbolTable();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        LinkedList<Token> line = interpreter.getLine();

        Token lValue = line.get(currentTokenIndex - 1);
        Token rValue = line.get(currentTokenIndex + 1);

        // remove tokens from line
        line.remove(currentTokenIndex);
        line.remove(rValue);
        line.remove(lValue);

        // check if lValue is actually an lvalue
        if (lValue.getType() != Token.TokenType.TXT)
            throw new NotLValueException(lValue + " is not an lvalue");

        // check if a variable type is specified
        if (currentTokenIndex - 2 >= 0) {
            Token type = line.get(currentTokenIndex - 2);

            // check if the type is actually a type
            if (type.getType() == Token.TokenType.TYPE) {

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

        // whereas if a variable type is not specified
        Variable variable = new Variable(
                rValue.getVarValue(runtime),
                rValue.getVarType(runtime),
                Accessibility.PUBLIC);
        table.updateVariable((String) lValue.getValue(), variable);

    }

}
