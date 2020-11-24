package org.gargiolang;

import org.gargiolang.environment.Environment;
import org.gargiolang.runtime.Runtime;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GargioLang {

    //C:\Users\konra\Desktop\Programming\gargiolang\test.gl

    public static void main(String[] args) throws Exception {

        Environment environment = new Environment();
        Runtime runtime = new Runtime(environment);

        // parse arguments
        int varIndex = Arrays.asList(args).indexOf("-var"); // variables to be passed (-var var1=value1,var2=value2)
        if (varIndex != -1) {
            String[] vars = args[varIndex+1].split(",");
            environment.loadVariables(vars);
        }

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
            runtime.run();

        }

        System.out.println(runtime.getSymbolTable().getVariable("var").getValue());
    }
}
