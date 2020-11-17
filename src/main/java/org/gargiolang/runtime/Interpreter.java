package org.gargiolang.runtime;

import org.gargiolang.lang.Token;

import java.util.LinkedList;

public class Interpreter {

    private final Runtime runtime;
    private final LinkedList<LinkedList<Token>> tokens;

    public Interpreter(Runtime runtime, LinkedList<LinkedList<Token>> tokens) {
        this.runtime = runtime;
        this.tokens = tokens;
    }

    public void execute() {
        int lineIndex = 0;

    }
}
