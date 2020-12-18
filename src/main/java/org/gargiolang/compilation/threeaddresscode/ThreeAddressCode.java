package org.gargiolang.compilation.threeaddresscode;

import org.gargiolang.compilation.Compiler;
import org.gargiolang.compilation.parser.Expression;
import org.gargiolang.compilation.parser.Operation;
import org.gargiolang.compilation.structures.trees.SyntaxNode;
import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.compilation.threeaddresscode.instructions.*;
import org.gargiolang.exception.parsing.UndeclaredSymbolException;

// TODO: 14/12/20 add support for floating point numbers (maybe with different instructions, as it is with traditional assembly (e.g. fl = float load))
public class ThreeAddressCode {

    /** linked list of instructions */
    Instruction instructions;
    Instruction lastInstruction;

    public ThreeAddressCode(SyntaxTree syntaxTree) throws UndeclaredSymbolException {
        instructions = new Label();
        lastInstruction = instructions;

        for (SyntaxNode root = syntaxTree.getRoot(); root != null; root = root.getRight()) {
            // generate three address code for every root
            generate(root);

        }

    }

    /**
     * Recursively generate code for every node
     */
    private void generate(SyntaxNode node) throws UndeclaredSymbolException {

        // literals do not generate code
        if (node.getOperation().isValue())
            return;

        // some useful references
        SyntaxNode[] children = (SyntaxNode[]) node.getValue();

        // first generate code recursively for every child (they have higher execution priority)
        for (SyntaxNode child : children) {
            generate(child);
        }

        // actually generate code based on the operation (simplify complex operations)
        switch (node.getOperation()) {
            case SUM -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int value = ((Literal) a).value + ((Literal) b).value;
                    Compiler.symbolTable().getSymbol(Address.result.address).value = value;
                    addInstruction(new Assign(Address.result, new Literal(value)));
                    return;
                }

                addInstruction(new Add(Address.result, a, b));

            }
            case SUBTRACTION -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int value = ((Literal) a).value - ((Literal) b).value;
                    Compiler.symbolTable().getSymbol(Address.result.address).value = value;
                    addInstruction(new Assign(Address.result, new Literal(value)));
                    return;
                }

                addInstruction(new Subtract(Address.result, a, b));
            }
            case INCREMENT -> {
                Address a = Address.fromSyntaxNode(node, 0);

                // if variable a is already defined --> increment value right away
                if (Compiler.symbolTable().isDefined(a.address)) {
                    Compiler.symbolTable().getSymbol(a.address).value ++;
                    // do not generate any code
                    return;
                }

                addInstruction(new Add(a, a, Literal.one));
            }
            case DECREMENT -> {
                Address a = Address.fromSyntaxNode(node, 0);

                // if variable a is already defined --> increment value right away
                if (Compiler.symbolTable().isDefined(a.address)) {
                    Compiler.symbolTable().getSymbol(a.address).value --;
                    // do not generate any code
                    return;
                }

                addInstruction(new Subtract(a, a, Literal.one));
            }
            case ASSIGNMENT -> {
                Address a = Address.fromSyntaxNode(node, 0);
                Value b;

                // if rvalue 'b' is an identifier (variable)
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if variable is defined in SymbolTable (that's the case for constants and local variables)
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue())) {
                        // assign the known value of 'b' to variable 'a'
                        Compiler.symbolTable().getSymbol(a.address).define(
                                Compiler.symbolTable().getSymbol((String) children[1].getValue()).value
                        );
                        // do not generate any instruction
                        return;
                    }
                    // whereas if variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {
                    b = Address.result;
                }
                // if rvalue is not an identifier, nor a returned value --> it's a literal value
                else {

                    // assign the literal value to 'a'
                    Compiler.symbolTable().getSymbol(a.address).define(
                            (int) children[1].getValue()
                    );
                    // do not generate any instruction
                    return;
                }

                addInstruction(new Assign(a, b));
            }
            case MULTIPLICATION -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    addInstruction(new Assign(Address.result, new Literal(((Literal) a).value * ((Literal) b).value)));
                    return;
                }

                fromComplexOperation(Operation.MULTIPLICATION, Address.result, a, b);
            }
            case NOT -> {
                Value a;

                if (children[0].getType() == Expression.IDENTIFIER) {
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue())) {
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // negate literal value
                        int notA;
                        if (((Literal) a).value == 0) notA = 0;
                        else notA = 1;
                        // return result as assignment instruction (result = !a)
                        addInstruction(new Assign(Address.result, new Literal(notA)));
                        // do not generate further instructions
                        return;
                    }
                    else {
                        a = Address.fromSyntaxNode(node, 0);
                    }
                }
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                    int notA;
                    if (((Literal) a).value == 0) notA = 0;
                    else notA = 1;
                    addInstruction(new Assign(Address.result, new Literal(notA)));
                    return;
                }

                fromComplexOperation(Operation.NOT, Address.result, a, null);
            }
            case NOT_EQUALS_TO -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value != ((Literal) b).value) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.NOT_EQUALS_TO, Address.result, a, b);
            }
            case GREATER_THAN -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value > ((Literal) b).value) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                addInstruction(new GreaterThan(Address.result, a, b));
            }
            case LESS_OR_EQUAL -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value <= ((Literal) b).value) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.LESS_OR_EQUAL, Address.result, a, b);
            }
            case GREATER_OR_EQUAL -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value >= ((Literal) b).value) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.GREATER_OR_EQUAL, Address.result, a, b);
            }
            case AND -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value != 0 && ((Literal) b).value != 0) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.AND, Address.result, a, b);
            }
            case OR -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value != 0 || ((Literal) b).value != 0) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.OR, Address.result, a, b);
            }
            case POWER -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    addInstruction(new Assign(Address.result, new Literal((int) Math.round(Math.pow(((Literal) a).value, ((Literal) b).value)))));
                    return;
                }

                fromComplexOperation(Operation.POWER, Address.result, a, b);
            }
            case ADDRESS_OF -> {
                Address.result = Address.fromSyntaxNode(node, 0);
            }
            case LESS_THAN -> {
                Value a;
                Value b;

                // if a is a variable
                if (children[0].getType() == Expression.IDENTIFIER) {
                    // check if a is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[0].getValue()))
                        a = new Literal(Compiler.symbolTable().getSymbol((String) children[0].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        a = Address.fromSyntaxNode(node, 0);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[0].getOperation() == Operation.VALUE) {
                    a = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    a = Literal.fromSyntaxNode(node, 0);
                }

                // if b is a variable
                if (children[1].getType() == Expression.IDENTIFIER) {
                    // check if b is defined in SymbolTable
                    if (Compiler.symbolTable().isDefined((String) children[1].getValue()))
                        b = new Literal(Compiler.symbolTable().getSymbol((String) children[1].getValue()).value);
                        // whereas the variable is not defined --> use its reference
                    else
                        b = Address.fromSyntaxNode(node, 1);
                }
                // if rvalue is a value returned from another operation (e.g. c = a-b)
                else if (children[1].getOperation() == Operation.VALUE) {// TODO: 18/12/20 use temp variables stack instead of Address.result
                    b = Address.result;
                }
                // if b is not a variable, nor a returned value --> it's a literal, so treat it as such
                else {
                    b = Literal.fromSyntaxNode(node, 1);
                }

                // if both operands are literals --> calculate the result right away
                if (a instanceof Literal && b instanceof Literal) {
                    int result;
                    if (((Literal) a).value > ((Literal) b).value) result = 1;
                    else result = 0;
                    addInstruction(new Assign(Address.result, new Literal(result)));
                    return;
                }

                fromComplexOperation(Operation.LESS_THAN, Address.result, a, b);
            }
        }
    }


    public void addInstruction(Instruction instruction) {
        lastInstruction.add(instruction);
        lastInstruction = instruction;
    }

    private void fromComplexOperation(Operation operation, Address result, Value op1, Value op2) {
        switch (operation) {
            case NOT -> {
                /*
                    simplification of NOT
                    a == false

                    three address code for NOT
                    r = op1 == op2
                 */

                addInstruction(new EqualsTo(result, op1, Literal.zero));
            }
            case NOT_EQUALS_TO -> {
                /*
                    simplification of !=
                    !(a == b)
                    (a == b) == false

                    three address code for !=
                    r = op1 == op2
                    r = r == 0
                 */

                addInstruction(new EqualsTo(result, op1, op2));
                addInstruction(new EqualsTo(result, result, Literal.zero));
            }
            case MULTIPLICATION -> {
                /*
                    three address code for MULTIPLICATION
                    r = 0
                    t1 = b
                L1:
                    t2 = t1 == 0
                    if t2 goto L2
                    r = r + a
                    t1 = t1 - 1
                    goto L1
                L2:

                 */

                // ask for some temporary variables
                Address t1 = Address.temp();
                Address t2 = Address.temp();

                Label l1 = new Label();

                addInstruction(Assign.zero(Address.result));                    //      r = 0
                addInstruction(Assign.zero(t1));                                //      t1 = 0
                addInstruction(l1);                                             // L1:
                addInstruction(new Add(Address.result, Address.result, op1));   //      r = r + a
                addInstruction(new Add(t1, t1, Literal.one));                   //      t1 = t1 + 1
                fromComplexOperation(Operation.NOT_EQUALS_TO, t2, t1, op1);     //      t2 = t1 != b
                addInstruction(new IfGoto(t2, l1));                             //      if t2 goto L1
            }
            case LESS_THAN -> {
                /*
                    simplification of <
                    a < b
                    !(a >= b)

                    three address code for <
                    r = a >= b
                    r = !r
                 */

                fromComplexOperation(Operation.GREATER_OR_EQUAL, result, op1, op2);
                fromComplexOperation(Operation.NOT, result, result, null);
            }
            case GREATER_OR_EQUAL -> {
                /*
                    simplification of >=
                    a >= b
                    (a > b) || (a == b) --> a > (b - 1)

                    three address code for >=
                    r = b - 1
                    r = a > r
                 */

                addInstruction(new Subtract(result, op2, Literal.one));
                addInstruction(new GreaterThan(result, op1, result));
            }
            case LESS_OR_EQUAL -> {
                /*
                    simplification of <=
                    a <= b
                    !(a > b)

                    three address code for <=
                    r = a > b
                    r = !r
                 */

                addInstruction(new GreaterThan(result, op1, op2));
                fromComplexOperation(Operation.NOT, result, result, null);
            }
            case AND -> {
                /*
                    simplification of &&
                    a && b
                    a == true == b

                    three address code for &&
                    r = a == true
                    if r goto L1
                    goto L2
                L1:
                    r = a == b
                L2:

                 */

                Label l1 = new Label();
                Label l2 = new Label();

                addInstruction(new EqualsTo(result, op1, Literal.one));
                addInstruction(new IfGoto(result, l1));
                addInstruction(new Goto(l2));
                addInstruction(l1);
                addInstruction(new EqualsTo(result, op1, op2));
                addInstruction(l2);
            }
            case OR -> {
                /*
                    simplification of ||
                    a || b
                    (a == true) (a != b)

                    three address code for ||
                    r = a == true
                    if r goto L1
                    r = b == true
                L1:

                 */

                Label l1 = new Label();

                addInstruction(new EqualsTo(result, op1, Literal.one));
                addInstruction(new IfGoto(result, l1));
                addInstruction(new EqualsTo(result, op2, Literal.one));
                addInstruction(l1);
            }
            case POWER -> {
                /*
                    three address code for a ** b
                    r = 1
                    t1 = b
                L1:
                    t2 = t1 == 0
                    if t2 goto L2
                    r = r * a
                    t1 = t1 - 1
                    goto L1
                L2:
                 */

                Label l1 = new Label();
                Label l2 = new Label();

                Address t1 = Address.temp();
                Address t2 = Address.temp();

                addInstruction(new Assign(result, Literal.one));
                addInstruction(new Assign(t1, op2));
                addInstruction(l1);
                addInstruction(new EqualsTo(t2, t1, Literal.zero));
                addInstruction(new IfGoto(t2, l2));
                fromComplexOperation(Operation.MULTIPLICATION, result, result, op1);
                addInstruction(new Subtract(result, t1, Literal.one));
                addInstruction(new Goto(l1));
                addInstruction(l2);
            }
            case MODULUS -> {
                // TODO: 17/12/20 implement modulus
            }
        }
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Instruction instruction = instructions; instruction != null; instruction = instruction.next) {
            stringBuilder.append(instruction).append("\n");
        }

        return stringBuilder.toString();
    }

}
