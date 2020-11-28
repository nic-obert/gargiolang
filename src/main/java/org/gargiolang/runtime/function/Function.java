package org.gargiolang.runtime.function;

import org.gargiolang.exception.evaluation.IndexOutOfBoundsException;
import org.gargiolang.exception.evaluation.*;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.SymbolTable;
import org.gargiolang.runtime.variable.Variable;

import java.util.LinkedList;

public class Function {

    private final int lineIndex;
    private final LinkedList<Parameter> params;
    private final Variable.Type returnType;
    private final int startingIndex;

    public Function(int lineIndex, int startingIndex, LinkedList<Parameter> params, Variable.Type returnType) {
        this.lineIndex = lineIndex;
        this.startingIndex = startingIndex;
        this.params = params;
        this.returnType = returnType;
    }

    public void call(Interpreter interpreter, LinkedList<Variable> args) throws InvalidArgumentsException, BadTypeException, VariableRedeclarationException, IndexOutOfBoundsException {

        /*
            - create new Call (store info about the current interpreter state)
            - push the new Call to the CallStack
            - push new Scope
            - initialize parameters (if any)
            - set line execution to the function's code block (not pushing a new Scope)
         */

        Runtime runtime = interpreter.getRuntime();


        // create new Call --> store info about the current interpreter state
        Call call = new Call(
                interpreter.getCurrentTokenIndex(),
                interpreter.getLineIndex(),
                runtime.getSymbolTable().scopeCount(),
                this,
                interpreter.getLine()
            );


        // push call to the call stack
        runtime.getCallStack().push(call);


        // push new scope to the stack
        SymbolTable symbolTable = runtime.getSymbolTable();
        symbolTable.pushScope();


        // initialize parameters (if any)

        // ensure argument count matches parameter count
        if (args.size() != params.size())
            throw new InvalidArgumentsException("Argument count (" + args.size() + ") does not match parameter count (" + params.size() + ")");

        for (int i = 0; i != params.size(); i++) {
            Parameter param = params.get(i);
            Variable arg = args.get(i);

            // check if types match
            if (!arg.getType().equals(param.getType()))
                throw new BadTypeException("Parameter '" + param.getName() + "' requires argument of type '" + param.getType() + "', but type '" + arg.getType() + "' was provided instead");

            // initialize parameter
            symbolTable.addVariable(param.getName(), arg);
        }


        // set line execution to the function's code block (not including the Scope)
        interpreter.setLineFrom(lineIndex, startingIndex + 1);

    }


    public Variable.Type getReturnType() {
        return returnType;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<Function: ").append(returnType).append(" (");

        params.forEach(param -> stringBuilder.append(param.getType()).append(" ").append(param.getName()).append(", "));

        return stringBuilder.append(") at (").append(lineIndex).append(", ").append(startingIndex).append(")>").toString();
    }

}
