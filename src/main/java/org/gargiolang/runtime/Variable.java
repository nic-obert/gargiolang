package org.gargiolang.runtime;

import org.gargiolang.lang.Token;
import org.gargiolang.lang.exception.GargioniException;

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


    public String toString() {
        return "{ " + type + ", " + value + ", " + accessibility + " }";
    }


    public enum Type {

        INT, FLOAT, STRING, BOOLEAN, NULL, DEF;

        public static Type getType(String str){
            for(Type t : Type.values()) if(t.toString().equalsIgnoreCase(str)) return t;
            return null;
        }

        // extract Variable.Type from Token
        public static Type extractVarType(Token token) throws GargioniException {
            switch (token.getType())
            {
                case NUM:
                    // TODO optimize here by removing conversion to string
                    if (token.getValue().toString().contains("\\.")) return FLOAT;
                    else return INT;

                case STR:
                    return STRING;

                case BOOL:
                    return BOOLEAN;

                default:
                    throw new GargioniException("Cannot extract variable type from token: " + token);
            }
        }

    }

}
