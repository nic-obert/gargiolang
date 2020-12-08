package org.gargiolang;

import org.gargiolang.compilation.Compiler;
import org.gargiolang.environment.Environment;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.tokenizer.Lexer;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GargioLang {

    //C:\Users\konra\Desktop\Programming\gargiolang\test.gl

    public static void main(String[] args) throws Exception {

        Environment environment = new Environment();
        Runtime runtime = new Runtime(environment);

        // parse arguments
        List<String> arguments = Arrays.asList(args);
        int varIndex = arguments.indexOf("-var"); // variables to be passed (-var var1=value1,var2=value2)
        if (varIndex != -1) {
            String[] vars = args[varIndex+1].split(",");
            environment.loadVariables(vars);
        }

        boolean doCompile = arguments.contains("-c"); // whether to interpret or to compile the program

        // if no file is specified --> launch interactive shell
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("GargioLang interactive shell");
            while (true) {
                System.out.print("> ");

                String statement;
                try { statement = scanner.nextLine(); }
                // exception caused by CTRL + D used to get out of the shell
                catch (NoSuchElementException e) { break; }

                runtime.loadStatement(statement);
                runtime.run();
            }

        }
        // if a file is specified --> execute it
        else {

            runtime.loadScript(args[0]);

            if (doCompile) {
                Compiler.compile(new Lexer(runtime.getStatements(), runtime).tokenize(), runtime.getLabelTable());

            } else {
                // normal interpreter
                runtime.run();
            }
        }

    }
}
