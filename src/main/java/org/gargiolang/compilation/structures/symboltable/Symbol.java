package org.gargiolang.compilation.structures.symboltable;

import org.gargiolang.compilation.parser.Expression;

public class Symbol {

    public Expression type;
    public int value;
    public boolean isDefined = false;

    public Symbol(Expression type) {
        this.type = type;
    }

    public void define(int value) {
        this.value = value;
        isDefined = true;
    }

    public String toString() {
        if (isDefined)
            return "<" + type + " " + value + ">";
        return "<" + type + " >";
    }

}
