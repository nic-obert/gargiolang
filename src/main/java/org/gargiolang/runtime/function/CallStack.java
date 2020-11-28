package org.gargiolang.runtime.function;

import org.gargiolang.exception.evaluation.EmptyCallStackException;

import java.util.Stack;

public class CallStack {

    private final Stack<Call> callStack;

    public CallStack() {
        this.callStack = new Stack<>();
    }

    public void push(Call call) {
        this.callStack.push(call);
    }

    public Call pop() throws EmptyCallStackException {
        if (callStack.isEmpty()) throw new EmptyCallStackException("Cannot pop from call stack: it's empty");
        return this.callStack.pop();
    }

}
