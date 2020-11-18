package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;

public class Preprocessor {

    public static String[] process(String[] script) throws GargioniException {
        String[] statements = script.clone();

        for (int i = 0; i != statements.length; i++) {

            String statement = script[i];

            if (statement.stripLeading().startsWith("#")) {
                int indexOfHash = statement.indexOf('#') + 1;
                int indexOfData = statement.indexOf(' ', indexOfHash) + 1;

                Processor preprocessor = Processor.valueOf(statement.substring(indexOfHash, indexOfData - 1).toUpperCase());
                String data = statement.substring(indexOfData);

                Processor.process(statements, preprocessor, data);
                // clear the statement
                statements[i] = "";
            }

        }

        return statements;
    }


    public enum Processor {

        DEFINE,
        INCLUDE,
        IFDEF,
        IFNDEF,
        ENDIF;

        public static void process(String[] statements, Processor processor, String data) throws GargioniException {
            switch (processor)
            {
                case DEFINE:
                    String word = data.substring(0, data.indexOf(' '));
                    String definition = data.substring(data.indexOf(' ')+1);
                    for (int i = 0; i != statements.length; i++) {
                        statements[i] = statements[i].replaceAll(word, definition);
                    }
                    break;

                case ENDIF:
                    break;

                case IFDEF:
                    break;

                case INCLUDE:
                    break;

                case IFNDEF:
                    break;

                default:
                    throw new GargioniException("Unrecognized preprocessor");
            }
        }
    }

}
