package org.gargiolang.compilation.threeaddresscode.instructions;

/**
 * Node of linked list of instructions which are the building blocks of generated intermediate code
 */
public abstract class Instruction {

    public Instruction next;

    public Instruction() {

    }

    /**
     * Appends the provided instruction to the end of the linked list
     *
     * @param instruction instruction to append to the end of the linked list
     */
    public void add(Instruction instruction) {
        if (next == null)
            next = instruction;
        else
            next.add(instruction);
    }


    public abstract String toString();

}
