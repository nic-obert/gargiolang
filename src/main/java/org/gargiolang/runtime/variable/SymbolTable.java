package org.gargiolang.runtime.variable;

import org.gargiolang.exception.evaluation.BadTypeException;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.VariableRedeclarationException;

import java.util.HashMap;

// TODO: 07/12/20 implement optimized data structure from scratch
/**
 * Wrapper around a Map
 */
public final class SymbolTable {

    private final HashMap<String, Variable> variables;

    private int scopes;


    public SymbolTable() {
        this.scopes = 0;
        this.variables = new HashMap<>();
    }


    /**
     * Returns an hashed version of the variable name to avoid naming conflicts
     * for the same variable name in different scopes
     *
     * @param varName the variable name to be hashed
     * @return the hashed variable name
     */
    private String hash(String varName) {
        return varName + "@" + scopeCount();
    }


    public void addVariable(String name, Variable variable) throws VariableRedeclarationException {
        String hashed = hash(name);
        if (variables.containsKey(hashed)) // look only in the current scope, do not use getVariable()
            throw new VariableRedeclarationException("Variable '" + name + "' is already declared in the scope: " + hashed.split("@")[1]);
        variables.put(hashed, variable);
    }


    public Variable getVariable(String varName) {
        Variable variable = variables.getOrDefault(hash(varName), null);
        // search in other scopes in case var is not found in current scope
        if (variable == null) {
            for (String key : variables.keySet()) {
                if (key.split("@")[0].equals(varName))
                    return variables.get(key);
            }
        }
        return variable;
    }

    /**
     * Wrapper around getVariable(String varName)
     *
     * @param varName variable name to get
     * @return variable to get
     * @throws UndeclaredVariableException if the variable is not declared in scope
     */
    public Variable getVariableThrow(String varName) throws UndeclaredVariableException {
        Variable variable = getVariable(varName);
        if (variable == null)
            throw new UndeclaredVariableException("Variable '" + varName + "' hasn't been declared in the scope");
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
        if (original.getType() != variable.getType()) {
            throw new BadTypeException("Variable types do not match: " + original.getType() + ", " + variable.getType() + ")");
        }

        variables.put(hash(varName), variable);
    }

    public void pushScope() {
        scopes ++;
    }

    public void popScope() {
        this.scopes --;

        // remove variables from table that belong to the popped scope
        variables.keySet().removeIf(key -> Integer.parseInt(key.split("@")[1]) > this.scopes);
    }

    public void popScopes(int count) {
        this.scopes -= count;
        variables.keySet().removeIf(key -> Integer.parseInt(key.split("@")[1]) > scopes);

    }

    public int scopeCount() {
        return this.scopes;
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
