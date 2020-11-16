package org.gargiolang.lang;

public class Variable {

    private final String name;
    private Object value;
    private Accessibility accessibility;

    public Variable(String name, Object value, Accessibility accessibility) {
        this.name = name;
        this.value = value;
        this.accessibility = accessibility;
    }

    public String getName() {
        return name;
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
}
