package org.gargiolang.compilation.structures.symboltable;

import java.util.HashMap;

public class SymbolTable {

    private final HashMap<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public Symbol getSymbol(String name) {
        return table.get(name);
    }

    public void declare(String name, Symbol symbol) {
        table.put(name, symbol);
    }

}
