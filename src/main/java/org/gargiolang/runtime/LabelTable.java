package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;

import java.util.HashMap;

public class LabelTable {

    private final HashMap<String, Integer> labels;

    public LabelTable() {
        this.labels = new HashMap<>();
    }

    public int getLabel(String label) throws GargioniException {
        Integer line = labels.get(label);
        if (line == null) throw new GargioniException("Label has not been defined: '" + label + "'");
        return labels.get(label);
    }

    public void putLabel(String label, int line) throws GargioniException {
        if (labels.containsKey(label)) throw new GargioniException("Label '" + label + "' is already defined");
        labels.put(label, line);
    }
}
