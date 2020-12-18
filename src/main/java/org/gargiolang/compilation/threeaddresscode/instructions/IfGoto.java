package org.gargiolang.compilation.threeaddresscode.instructions;

import org.gargiolang.compilation.threeaddresscode.Value;

public class IfGoto extends Instruction {

    public final Value condition;
    public final Label label;

    public IfGoto(Value condition, Label label) {
        this.condition = condition;
        this.label = label;
    }

    @Override
    public String toString() {
        return "if " + condition + " goto " + label;
    }

}
