package org.gargiolang.runtime;

import org.gargiolang.lang.Parser;
import org.gargiolang.lang.exception.GargioniException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public final class Runtime {

    private static Runtime runtime;

    private final SymbolTable symbolTable;

    public Runtime(){
        runtime = this;

        this.symbolTable = new SymbolTable();
    }

    public void runFile(String string) throws GargioniException, IOException {
        final File file = new File(string);

        if(!file.exists()){
            throw new GargioniException("The specified file couldn't be found (" + string + ").");
        }

        final byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append((char) aByte);
        }

        final LinkedList<String> tempStatements = new LinkedList<>();
        for(String s : builder.toString().replaceAll("\n", "").split(";")){
            if(!s.startsWith("//") && !s.isEmpty()){
                tempStatements.add(s.replaceAll("\n", ""));
            }
        }
        final String[] statements = tempStatements.toArray(new String[0]);
        for (String statement : statements) {
            System.out.println(statement);
        }

        Parser parser = new Parser(statements);
        parser.parseTokens();
        System.out.println(parser.getTokens());
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static Runtime getRuntime() {
        return runtime;
    }
}
