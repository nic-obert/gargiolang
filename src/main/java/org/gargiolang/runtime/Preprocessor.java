package org.gargiolang.runtime;

import org.gargiolang.environment.Environment;
import org.gargiolang.lang.exception.GargioniException;

import java.util.LinkedList;

public class Preprocessor {

    public static void process(LinkedList<String> statements) throws GargioniException {

        for (String statement : statements) {

            if (statement.stripLeading().startsWith("#")) {
                int indexOfHash = statement.indexOf('#') + 1;
                int indexOfData = statement.indexOf(' ', indexOfHash) + 1;

                String preprocessor;
                String data = null;
                if (indexOfData == 0) preprocessor = statement.substring(indexOfHash);
                else {
                    preprocessor = statement.substring(indexOfHash, indexOfData - 1);
                    data = statement.substring(indexOfData); // kind of the arguments to the preprocessor
                }

                switch (preprocessor)
                {
                    case "define": {
                        assert data != null;
                        String word = data.substring(0, data.indexOf(' '));
                        String definition = data.substring(data.indexOf(' ') + 1);
                        // replace all occurrences of word with definition
                        statements.forEach(line -> line = line.replaceAll(word, definition));
                        // finally remove the preprocessor
                        statements.set(statements.indexOf(statement), "");
                        break;
                    }


                    case "ifdef": {
                        assert data != null;
                        String var = data.strip();
                        boolean doRemove = !Environment.getInstance().getVariables().containsKey(var);

                        // search for closing #endif
                        int ifCount = 1;
                        int index = statements.indexOf(statement);
                        // remove preprocessor
                        statements.set(index, "");

                        for (index++; true; index++) {
                            String line = statements.get(index).stripLeading();

                            if (line.startsWith("#if")) {
                                ifCount++;
                            } else if (line.startsWith("#endif")) {
                                ifCount--;
                                if (ifCount == 0) {
                                    // remove #endif preprocessor
                                    statements.set(index, "");
                                    break;
                                }
                            }

                            if (doRemove) statements.set(index, "");
                        }
                        break;
                    }


                    case "include": {
                        break;
                    }


                    case "ifndef": {
                        assert data != null;
                        String var = data.strip();
                        boolean doRemove = Environment.getInstance().getVariables().containsKey(var);

                        // search for closing #endif
                        int ifCount = 1;
                        int index = statements.indexOf(statement);
                        // remove preprocessor
                        statements.set(index, "");

                        for (index++; true; index++) {
                            String line = statements.get(index).stripLeading();

                            if (line.startsWith("#if")) {
                                ifCount++;
                            } else if (line.startsWith("#endif")) {
                                ifCount--;
                                if (ifCount == 0) {
                                    // remove #endif preprocessor
                                    statements.set(index, "");
                                    break;
                                }
                            }

                            if (doRemove) statements.set(index, "");
                        }
                        break;
                    }


                    case "endif":
                        // do nothing
                        break;


                    default:
                        throw new GargioniException("Unrecognized preprocessor");
                }
            }
        }
    }
}
