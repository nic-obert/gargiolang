package org.gargiolang.tokenizer;

import org.gargiolang.exception.evaluation.UndefinedLabelException;
import org.gargiolang.exception.tokenization.LabelRedefinitionException;

import java.util.HashMap;

public class LabelTable {

    private final HashMap<String, Integer> labels;

    public LabelTable() {
        this.labels = new HashMap<>();
    }

    public int getLabel(String label) throws UndefinedLabelException {
        Integer line = labels.get(label);
        if (line == null)
            throw new UndefinedLabelException("Label is not defined: '" + label + "'");
        return labels.get(label);
    }

    public void putLabel(String label, int line) throws LabelRedefinitionException {
        if (labels.containsKey(label))
            throw new LabelRedefinitionException("Label '" + label + "' is already defined");
        labels.put(label, line);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        for (String variable : labels.keySet()) {
            stringBuilder.append("\t").append(variable).append(": ").append(labels.get(variable)).append(",\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
