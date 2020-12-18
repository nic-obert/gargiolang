package org.gargiolang.compilation.threeaddresscode.instructions;

public class Label extends Instruction {

    public Label() {

    }

    @Override
    public String toString() {
        return "Label " + this.hashCode() + ":";
    }

}
