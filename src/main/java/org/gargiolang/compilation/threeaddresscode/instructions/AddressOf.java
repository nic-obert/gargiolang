package org.gargiolang.compilation.threeaddresscode.instructions;

import org.gargiolang.compilation.threeaddresscode.Address;

public class AddressOf extends Instruction {

    public final Address result;
    public final Address a;

    public AddressOf(Address result, Address a) {
        this.result = result;
        this.a = a;
    }

    @Override
    public String toString() {
        return result + " = &" + a;
    }

}
