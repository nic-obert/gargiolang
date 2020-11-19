package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;

import java.util.HashMap;
import java.util.Map;

// wrapper around a Map
public final class SymbolTable {

    private final Map<String, Variable> variables = new HashMap<>();

    public void addVariable(String name, Variable variable){
        variables.putIfAbsent(name, variable);
    }

    public Variable getVariable(String varName) {
        return variables.getOrDefault(varName, null);
    }

    public Variable getVariableThrow(String varName) throws GargioniException {
        Variable variable = variables.getOrDefault(varName, null);
        if (variable == null) throw new GargioniException("Variable '" + varName + "' hasn't been declared");
        return variable;
    }

    public void updateVariable(String varName, Variable variable) throws GargioniException {
        Variable original = getVariable(varName);

        // since GargioLang is statically typed, you cannot assign a different type from the one the variable was first initialized with
        if (original != null && !original.getType().equals(variable.getType())) {
            throw new GargioniException("Variable types do not match: " + original.getType() + ", " + variable.getType() + ")");
        }

        variables.put(varName, variable);
    }
}
