package org.gargiolang.compilation.threeaddresscode.instructions;

import org.gargiolang.compilation.threeaddresscode.Address;
import org.gargiolang.compilation.threeaddresscode.Literal;
import org.gargiolang.compilation.threeaddresscode.Value;

public class Assign extends Instruction {

    public final Address a;
    public final Value b;

    public static Assign zero(Address address) {
        return new Assign(address, new Literal(0));
    }

    public Assign(Address a, Value b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a + " = " + b;
    }

}
