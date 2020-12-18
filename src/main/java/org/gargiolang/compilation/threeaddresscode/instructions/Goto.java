package org.gargiolang.compilation.threeaddresscode.instructions;

public class Goto extends Instruction {

    public Label label;

    public Goto(Label label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "goto " + label;
    }

}
