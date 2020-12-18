package org.gargiolang.compilation.threeaddresscode.instructions;

import org.gargiolang.compilation.threeaddresscode.Address;
import org.gargiolang.compilation.threeaddresscode.Value;

public class Add extends Instruction{

    public final Address result;
    public final Value a;
    public final Value b;

    public Add(Address result, Value a, Value b) {
        this.result = result;
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return result + " = " + a + " + " + b;
    }
}
