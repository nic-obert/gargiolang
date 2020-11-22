package org.gargiolang.runtime;

import org.gargiolang.dependencies.Dependency;
import org.gargiolang.environment.Environment;
import org.gargiolang.lang.Parser;
import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.GargioniException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class Runtime {

    private static Runtime instance;

    private final LabelTable labelTable;

    private final Stack<Integer> gotoStack;

    private final Environment environment;

    private final SymbolTable symbolTable;

    private LinkedList<String> statements;

    private final List<Dependency> loadedDependencies = new ArrayList<>();


    public Runtime(Environment environment) {
        instance = this;

        this.environment = environment;
        this.symbolTable = new SymbolTable();
        this.statements = new LinkedList<>();
        this.labelTable = new LabelTable();
        this.gotoStack = new Stack<>();
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

        this.statements.addAll(Arrays.asList(builder.toString().split(";")));
    }


    public void loadStatement(String statement) {
        this.statements.add(statement);
    }


    public void run() throws GargioniException {

        Preprocessor.process(statements);

        LinkedList<LinkedList<Token>> tokens = new Parser(statements, this).parseTokens();

        System.out.println(tokens);

        Interpreter interpreter = new Interpreter(this, tokens);
        interpreter.execute();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static Runtime getRuntime() {
        return instance;
    }

    public List<Dependency> getLoadedDependencies() {
        return loadedDependencies;
    }

    public LabelTable getLabelTable() {
        return labelTable;
    }

    public Stack<Integer> getGotoStack() {
        return gotoStack;
    }
}
