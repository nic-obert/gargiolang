package org.gargiolang.compilation.structures.stacks;

import org.gargiolang.compilation.threeaddresscode.Address;

public class AddressNode {

    public AddressNode prev;
    public final Address address;

    public AddressNode(Address address) {
        this.address = address;
    }

}
