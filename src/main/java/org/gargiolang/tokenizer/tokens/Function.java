package org.gargiolang.tokenizer.tokens;

import org.gargiolang.exception.evaluation.*;
import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.Accessibility;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class Function {

    public static void evaluate(Interpreter interpreter) throws UndefinedFunctionException, VariableRedeclarationException, InvalidArgumentsException, UndeclaredVariableException, BadTypeException, UnrecognizedTypeException, IndexOutOfBoundsException {

        TokenLine line = interpreter.getLine();
        Token currentToken = interpreter.getCurrentToken();
        Runtime runtime = interpreter.getRuntime();


        // get function name
        String funcName = (String) currentToken.getValue();


        // get argument list
        LinkedList<Variable> args = new LinkedList<>();
        Token arg = currentToken.getNext().getNext();
        for ( ; arg.getType() != TokenType.CALL; arg = arg.getNext()) {

            args.add(new Variable(
                        arg.getVarValue(runtime),
                        arg.getVarType(runtime),
                        Accessibility.PUBLIC));
            line.remove(arg);
        }

        // remove evaluated tokens
        line.remove(arg);
        line.remove(currentToken.getNext());


        // get the function to call
        org.gargiolang.runtime.function.Function function = runtime.getFunctionTable().getFunction(funcName);


        // call the function
        function.call(interpreter, args);

    }

}
