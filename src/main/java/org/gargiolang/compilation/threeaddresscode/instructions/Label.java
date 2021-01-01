package org.gargiolang.compilation.threeaddresscode.instructions;

public class Label extends Instruction {

    public static Label main = new Label();

    public Label() {

    }

    @Override
    public String toString() {
        return "Label " + this.hashCode() + ":";
    }

}
