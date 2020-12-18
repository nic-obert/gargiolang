package org.gargiolang.compilation.structures.stacks;

import org.gargiolang.compilation.threeaddresscode.Address;

public class AddressStack {

    private AddressNode first;

    public AddressStack() {
        push(new Address("t8"));
        push(new Address("t7"));
        push(new Address("t6"));
        push(new Address("t5"));
        push(new Address("t4"));
        push(new Address("t3"));
        push(new Address("t2"));
        push(new Address("t1"));
    }

    public Address pop() {
        if (first == null)
            return null;
        Address address = first.address;
        first = first.prev;
        return address;
    }

    public void push(Address address) {
        if (first == null) {
            first = new AddressNode(address);
            return;
        }
        AddressNode node = new AddressNode(address);
        node.prev = first;
        first = node;
    }

}
