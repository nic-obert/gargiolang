package org.gargiolang.lang;

import java.util.HashMap;
import java.util.Map;

public final class SymbolTable {

    public final Map<String, Variable> variables = new HashMap<>();

    public Map<String, Variable> getVariables() {
        return variables;
    }
}
