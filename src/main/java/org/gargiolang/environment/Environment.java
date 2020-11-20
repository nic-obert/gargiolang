package org.gargiolang.environment;

import java.util.HashMap;

// class for environment variables, compile-time variables, to get OS...
public class Environment {

    private static Environment instance;
    private final HashMap<String, String> variables;

    public Environment() {
        instance = this;

        this.variables = new HashMap<>();
    }

    public void loadVariables(String[] vars) {
        for (String var : vars) {
            String[] keyValuePair = var.split("=");
            for (int i = 0; i != keyValuePair.length; i++) {
                variables.put(keyValuePair[0], keyValuePair[1]);
            }
        }
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public static Environment getInstance() {
        return instance;
    }
}
