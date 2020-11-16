package org.gargiolang.runtime;

public enum Accessibility {

    PUBLIC((byte) 0),
    PRIVATE((byte) 1),
    PROTECTED((byte) 2);

    private final byte accessibility;

    Accessibility(byte accessibility) {
        this.accessibility = accessibility;
    }
}
