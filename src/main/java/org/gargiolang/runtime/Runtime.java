package org.gargiolang.runtime;

import org.gargiolang.lang.Parser;
import org.gargiolang.lang.exception.GargioniException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Runtime {

    private static Runtime runtime;

    private final SymbolTable symbolTable;

    private String[] statements;

    public Runtime() {
        runtime = this;

        this.symbolTable = new SymbolTable();
    }

    public void loadScript(String scriptName) throws IOException, GargioniException {

        final File file = new File(scriptName);

        if(!file.exists()){
            throw new GargioniException("The specified file couldn't be found (" + scriptName + ").");
        }

        final byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append((char) aByte);
        }

        this.statements = builder.toString().split(";");
    }


    public void loadStatement(String statement) {
        this.statements = new String[]{statement};
    }


    public void run() throws GargioniException {

        Preprocessor preprocessor = new Preprocessor(statements);
        preprocessor.process();

        Parser parser = new Parser(preprocessor.getStatements());
        parser.parseTokens();

        System.out.println(parser.getTokens());

        Interpreter interpreter = new Interpreter(getRuntime(), parser.getTokens());
        interpreter.execute();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static Runtime getRuntime() {
        return runtime;
    }
}
