package org.gargiolang.runtime;

import org.gargiolang.lang.Keyword;

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
        return accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }


    public enum Type {

        INT, FLOAT, STRING, BOOLEAN, NULL, DEF;

        public static Type getType(String str){
            for(Type t : Type.values()) if(t.toString().equalsIgnoreCase(str)) return t;
            return null;
        }

    }

}
