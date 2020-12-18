package org.gargiolang.compilation.threeaddresscode;

import org.gargiolang.compilation.structures.stacks.AddressStack;
import org.gargiolang.compilation.structures.trees.SyntaxNode;

public class Address extends Value {

    // TODO: 14/12/20 optimize by replacing strings with ints
    public static Address result = new Address("r");

    public final String address;

    private static final AddressStack temps = new AddressStack();

    public Address(String address) {
        this.address = address;
    }

    public static Address fromSyntaxNode(SyntaxNode node, int index) {
        return new Address((String) ((SyntaxNode[]) node.getValue())[index].getValue());
    }

    /**
     * Generates a temporary variable
     *
     * @return a temporary address
     */
    public static Address temp() {
        return temps.pop();
    }

    /**
     * Frees a temporary address generated by Address.temp()
     *
     * @param address the temporary address to free
     */
    public static void free(Address address) {
        temps.push(address);
    }

    @Override
    public String toString() {
        return address;
    }
}
