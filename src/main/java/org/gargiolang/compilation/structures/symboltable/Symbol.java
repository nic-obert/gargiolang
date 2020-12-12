package org.gargiolang.compilation.structures.symboltable;

import org.gargiolang.compilation.parser.Expression;

public class Symbol {

    public Expression type;

    public Symbol(Expression type) {
        this.type = type;
    }

}
