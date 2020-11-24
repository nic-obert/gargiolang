package org.gargiolang.runtime.variable;

import org.gargiolang.lang.exception.evaluation.BadTypeException;
import org.gargiolang.lang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.lang.exception.evaluation.VariableRedeclarationException;
import org.gargiolang.runtime.variable.Variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

// wrapper around a Map
public final class SymbolTable {

    private final HashMap<String, Variable> variables;

    private final Stack<Integer> scopes;


    public SymbolTable() {
        this.scopes = new Stack<>();
        this.variables = new HashMap<>();
    }


    public void addVariable(String name, Variable variable) throws VariableRedeclarationException {
        if (variables.containsKey(name)) throw new VariableRedeclarationException("Variable '" + name + "' is already declared");
        variables.put(name, variable);
    }

    public Variable getVariable(String varName) {
        return variables.getOrDefault(varName, null);
    }

    public Variable getVariableThrow(String varName) throws UndeclaredVariableException {
        Variable variable = variables.getOrDefault(varName, null);
        if (variable == null) throw new UndeclaredVariableException("Variable '" + varName + "' hasn't been declared");
        return variable;
    }

    /**
     * Updates the specified variable with the given new variable.
     * Throws GargioniException if the variable is not declared.
     *
     * @param varName the name of the variable to update
     * @param variable the new value of the variable
     * @throws BadTypeException if variable types do not match
     * @throws UndeclaredVariableException if variable is undeclared
     */
    public void updateVariable(String varName, Variable variable) throws BadTypeException, UndeclaredVariableException {
        Variable original = getVariableThrow(varName);

        // since GargioLang is statically typed, you cannot assign a different type from the one the variable was first initialized with
        if (!original.getType().equals(variable.getType())) {
            throw new BadTypeException("Variable types do not match: " + original.getType() + ", " + variable.getType() + ")");
        }

        variables.put(varName, variable);
    }

    public void addScope() {
        scopes.push(variables.size());
    }

    public void popScope() {
        int scope = scopes.pop();
        int i = 1; // here 1 is used instead of 0 for optimization
        // remove variables from table that belong to the popped scope
        for (Iterator<String> iterator = variables.keySet().iterator(); iterator.hasNext(); i++) {
            iterator.next();
            if (i > scope) iterator.remove();
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        for (String variable : variables.keySet()) {
            stringBuilder.append("\t").append(variable).append(": ").append(variables.get(variable)).append(",\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}