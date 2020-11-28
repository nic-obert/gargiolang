package org.gargiolang.runtime.function;

import org.gargiolang.lang.Token;
import org.gargiolang.runtime.variable.Variable;

import java.util.Arrays;
import java.util.LinkedList;

public class Function {

    private final int lineIndex;
    private final LinkedList<Token[]> args;
    private final Variable.Type returnType;
    private final int startingIndex;

    public Function(int lineIndex, int startingIndex, LinkedList<Token[]> args, Variable.Type returnType) {
        this.lineIndex = lineIndex;
        this.startingIndex = startingIndex;
        this.args = args;
        this.returnType = returnType;
    }

    public void call() {
        // TODO to implement
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<Function: ").append(returnType).append(" (");

        args.forEach(arg -> stringBuilder.append(arg[0].getValue()).append(" ").append(arg[1].getValue()).append(", "));

        return stringBuilder.append(") at (").append(lineIndex).append(", ").append(startingIndex).append(")>").toString();
    }

}
