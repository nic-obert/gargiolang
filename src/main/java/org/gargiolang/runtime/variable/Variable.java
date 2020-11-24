package org.gargiolang.runtime.variable;

import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.evaluation.*;
import org.gargiolang.runtime.Runtime;
import org.gargiolang.runtime.variable.types.Float;
import org.gargiolang.runtime.variable.types.Integer;
import org.gargiolang.runtime.variable.types.String;

public class Variable {

    private Object value;
    private final Variable.Type type;
    private Accessibility accessibility;


    public Variable(Object value, Variable.Type type, Accessibility accessibility) {
        this.value = value;
        this.type = type;
        this.accessibility = accessibility;
    }


    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Accessibility getAccessibility() {
        return this.accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }


    public java.lang.String toString() {
        return "{ " + type + ", " + value + ", " + accessibility + " }";
    }


    public enum Type {

        INT,
        FLOAT,
        STRING,
        BOOLEAN,
        NULL,
        DEF;


        public static Type getType(java.lang.String str){
            for(Type t : Type.values()) if(t.toString().equalsIgnoreCase(str)) return t;
            return null;
        }

        // extract Variable.Type from Token
        public static Type extractVarType(Token token) throws UnrecognizedTypeException {
            switch (token.getType())
            {
                case NUM:
                    // TODO optimize here by removing conversion to string
                    if (token.getValue().toString().contains(".")) return FLOAT;
                    else return INT;

                case STR:
                    return STRING;

                case BOOL:
                    return BOOLEAN;

                default:
                    throw new UnrecognizedTypeException("Cannot extract variable type from token: " + token);
            }
        }


        public Token sum(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            // TODO implement this using method overloading, reflection or this kind of stuff. I don't know if it's doable in java, but in C++ you can.

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (a.getVarType(runtime))
            {
                case INT -> result = Integer.add((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.add((double) a.getVarValue(runtime), b);

                case STRING -> result = String.add((java.lang.String) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token subtract(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            // TODO implement this using method overloading, reflection or this kind of stuff. I don't know if it's doable in java, but in C++ you can.

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (a.getVarType(runtime))
            {
                case INT -> result = Integer.subtract((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.subtract((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token multiply(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException {
            // TODO implement this using method overloading, reflection or this kind of stuff. I don't know if it's doable in java, but in C++ you can.

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (a.getVarType(runtime))
            {
                case INT -> result = Integer.multiply((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.multiply((double) a.getVarValue(runtime), b);

                case STRING -> result = String.multiply((java.lang.String) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }


        public Token divide(Token a, Token b) throws UnrecognizedTypeException, UndeclaredVariableException, UnhandledOperationException, UnimplementedException, ZeroDivisionException {
            // TODO implement this using method overloading, reflection or this kind of stuff. I don't know if it's doable in java, but in C++ you can.

            Runtime runtime = Runtime.getRuntime();
            Token result;

            switch (a.getVarType(runtime))
            {
                case INT -> result = Integer.divide((int) a.getVarValue(runtime), b);

                case FLOAT -> result = Float.divide((double) a.getVarValue(runtime), b);

                default -> throw new UnimplementedException("Unimplemented operation for type " + a.getVarType(runtime));
            }

            return result;
        }
    }
}