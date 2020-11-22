package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.lang.exception.evaluation.UndefinedLabelException;
import org.gargiolang.lang.exception.parsing.LabelRedefinitionException;

import java.util.HashMap;

public class LabelTable {

    private final HashMap<String, Integer> labels;

    public LabelTable() {
        this.labels = new HashMap<>();
    }

    public int getLabel(String label) throws UndefinedLabelException {
        Integer line = labels.get(label);
        if (line == null) throw new UndefinedLabelException("Label is not defined: '" + label + "'");
        return labels.get(label);
    }

    public void putLabel(String label, int line) throws LabelRedefinitionException {
        if (labels.containsKey(label)) throw new LabelRedefinitionException("Label '" + label + "' is already defined");
        labels.put(label, line);
    }
}
