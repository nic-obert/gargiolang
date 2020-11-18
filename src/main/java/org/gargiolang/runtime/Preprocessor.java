package org.gargiolang.runtime;

import org.gargiolang.lang.exception.GargioniException;

public class Preprocessor {

    private final String[] statements;

    public Preprocessor(String[] statements) {
        this.statements = statements;
    }

    public void process() throws GargioniException {
        for (String statement : this.statements) {
            if (statement.stripLeading().startsWith("#")) {
                int indexOfHash = statement.indexOf('#') + 1;
                int indexOfData = statement.indexOf(' ', indexOfHash) + 1;

                Processor preprocessor = Processor.valueOf(statement.substring(indexOfHash, indexOfData).toUpperCase());
                String data = statement.substring(indexOfData);

                Processor.process(preprocessor, data);
            }
        }
    }

    public String[] getStatements() {
        return this.statements;
    }


    public enum Processor {

        DEFINE,
        INCLUDE,
        IFDEF,
        IFNDEF,
        ENDIF;

        public static void process(Processor processor, String data) throws GargioniException {
            switch (processor)
            {
                case DEFINE:
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
