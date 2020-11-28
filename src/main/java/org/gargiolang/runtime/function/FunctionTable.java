package org.gargiolang.runtime.function;

import org.gargiolang.exception.evaluation.FunctionRedefinititionException;
import org.gargiolang.exception.evaluation.UndefinedFunctionException;

import java.util.HashMap;

public class FunctionTable {

    private final HashMap<String, Function> functionTable;

    public FunctionTable() {
        this.functionTable = new HashMap<>();
    }

    public void addFunction(String name, Function function) throws FunctionRedefinititionException {
        if (this.functionTable.containsKey(name))
            throw new FunctionRedefinititionException("Function " + name + " is already defined");
        this.functionTable.put(name, function);
    }

    public Function getFunction(String name) throws UndefinedFunctionException {
        Function function = this.functionTable.getOrDefault(name, null);
        if (function == null)
            throw new UndefinedFunctionException("Function is not defined: " + name);
        return function;
    }

}
