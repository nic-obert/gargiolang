package org.gargiolang.runtime;

import org.gargiolang.preprocessing.LabelTable;
import org.gargiolang.preprocessing.Preprocessor;
import org.gargiolang.preprocessing.dependencies.Dependency;
import org.gargiolang.environment.Environment;
import org.gargiolang.lang.Parser;
import org.gargiolang.lang.Token;
import org.gargiolang.exception.GargioniException;
import org.gargiolang.runtime.function.CallStack;
import org.gargiolang.runtime.function.FunctionTable;
import org.gargiolang.runtime.variable.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class Runtime {

    private static Runtime instance;

    private final LabelTable labelTable;

    private final Stack<Integer> gotoStack;

    private final CallStack callStack;

    private final FunctionTable functionTable;

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
        this.callStack = new CallStack();
        this.functionTable = new FunctionTable();
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


    public void run() throws GargioniException, ReflectiveOperationException, IOException {

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

    public FunctionTable getFunctionTable() {
        return functionTable;
    }

    public CallStack getCallStack() {
        return callStack;
    }
}
