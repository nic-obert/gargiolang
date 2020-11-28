package org.gargiolang.runtime.function;

import org.gargiolang.runtime.variable.Variable;

public class Parameter {

    private final String name;
    private final Variable.Type type;

    public Parameter(String name, Variable.Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Variable.Type getType() {
        return type;
    }

}
