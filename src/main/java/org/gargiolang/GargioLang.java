package org.gargiolang;

import org.gargiolang.runtime.Runtime;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class GargioLang {

    //C:\Users\konra\Desktop\Programming\gargiolang\test.gl

    public static void main(String[] args) throws Exception {

        Runtime runtime = new Runtime();

        // if no file is specified --> launch interactive shell
        if (args == null || args.length < 1) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("GargioLang interactive shell\n");
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
    }
}
