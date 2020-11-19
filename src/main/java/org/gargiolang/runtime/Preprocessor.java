package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;

import java.util.Arrays;
import java.util.LinkedList;

public class Preprocessor {

    public static LinkedList<String> process(String[] script) throws GargioniException {

        // convert String[] into linked list (for preprocessing)
        LinkedList<String> statements = new LinkedList<>(Arrays.asList(script));

        for (String statement : statements) {

            if (statement.stripLeading().startsWith("#")) {
                int indexOfHash = statement.indexOf('#') + 1;
                int indexOfData = statement.indexOf(' ', indexOfHash) + 1;

                String preprocessor = statement.substring(indexOfHash, indexOfData - 1);
                String data = statement.substring(indexOfData); // kind of the arguments to the preprocessor


                switch (preprocessor)
                {
                    case "define":
                        String word = data.substring(0, data.indexOf(' '));
                        String definition = data.substring(data.indexOf(' ')+1);
                        statements.forEach(line -> line = line.replaceAll(word, definition));
                        break;

                    case "endif":
                        break;

                    case "ifdef":
                        break;

                    case "include":
                        break;

                    case "ifndef":
                        break;

                    default:
                        throw new GargioniException("Unrecognized preprocessor");
                }


                // remove the preprocessor, once it's been processed
                statements.remove(statement);
            }

        }

        return statements;
    }
}
