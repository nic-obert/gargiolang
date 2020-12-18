package org.gargiolang.compilation.structures.symboltable;

import org.gargiolang.compilation.threeaddresscode.Address;
import org.gargiolang.exception.parsing.SymbolRedeclarationException;
import org.gargiolang.exception.parsing.UndeclaredSymbolException;

import java.util.HashMap;

// TODO: 12/12/20 implement a more optimized SymbolTable from scratch
public class SymbolTable {

    private final HashMap<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();

        // declare some global compiler variables
        table.put(Address.result.address, null);
    }

    public Symbol getSymbol(String name) throws UndeclaredSymbolException {
        Symbol symbol = table.getOrDefault(name, null);
        if (symbol == null)
            throw new UndeclaredSymbolException("Symbol " + name + " has not been declared");
        return symbol;
    }

    public void declare(String name, Symbol symbol) throws SymbolRedeclarationException {
        if (table.containsKey(name))
            throw new SymbolRedeclarationException("Symbol " + name + " is already declared in scope");
        table.put(name, symbol);
    }

    public boolean isDefined(String name) throws UndeclaredSymbolException {
        return getSymbol(name).isDefined;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        table.forEach((name, symbol) -> stringBuilder.append(name).append(": ").append(symbol).append("\n"));

        return stringBuilder.toString();
    }

}
