package org.gargiolang.lang;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Accessibility;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class Function {

    public static void evaluate(Interpreter interpreter) throws UndefinedFunctionException, VariableRedeclarationException, InvalidArgumentsException, UndeclaredVariableException, BadTypeException, UnrecognizedTypeException, IndexOutOfBoundsException {

        LinkedList<Token> line = interpreter.getLine();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        Runtime runtime = interpreter.getRuntime();


        // get function name
        String funcName = (String) line.get(currentTokenIndex).getValue();


        // get argument list
        LinkedList<Variable> args = new LinkedList<>();
        int argIndex = currentTokenIndex + 2;
        for (Token token = line.get(argIndex); !token.getType().equals(Token.TokenType.CALL); token = line.get(argIndex)) {

            args.add(new Variable(
                        token.getVarValue(runtime),
                        token.getVarType(runtime),
                        Accessibility.PUBLIC));
            line.remove(token);
        }

        // remove evaluated tokens
        line.remove(argIndex);
        line.remove(currentTokenIndex + 1);


        // get the function to call
        org.gargiolang.runtime.function.Function function = runtime.getFunctionTable().getFunction(funcName);


        // call the function
        function.call(interpreter, args);

    }

}
