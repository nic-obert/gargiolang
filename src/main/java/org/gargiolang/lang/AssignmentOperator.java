package org.gargiolang.lang;

import org.gargiolang.lang.exception.GargioniException;
import org.gargiolang.runtime.Accessibility;
import org.gargiolang.runtime.Interpreter;
import org.gargiolang.runtime.SymbolTable;
import org.gargiolang.runtime.Variable;

import java.util.LinkedList;

public class AssignmentOperator {

    public static void evaluate(Interpreter interpreter) throws GargioniException {
        //Code below will be implemented soon
        SymbolTable table = interpreter.getRuntime().getSymbolTable();
        int currentTokenIndex = interpreter.getCurrentTokenIndex();
        LinkedList<Token> line = interpreter.getLine();

        System.out.println(currentTokenIndex);

        Token after = line.get(currentTokenIndex + 1);
        Token before = line.get(currentTokenIndex - 1);

        if(after == null || before == null){
            throw new GargioniException("Invalid statement");
        }

        if(table.getVariable((String) before.getValue()) == null){
            if(line.size() == 3){
                throw new GargioniException("Type isn't provided");
            }

            table.addVariable((String) before.getValue(), new Variable(after.getValue(), Variable.Type.valueOf(line.get(currentTokenIndex - 2).getValue().toString().toUpperCase()), Accessibility.PUBLIC));

            //Lo ripeto due volte perchè mi bestemmia addosso per l'ordine
            line.remove(currentTokenIndex + 1);
            line.remove(currentTokenIndex);
            line.remove(currentTokenIndex - 1);
            line.remove(currentTokenIndex - 2);
        } else {
            if(line.size() == 4){
                throw new GargioniException("Variable \"" + before.getValue() + "\" is already defined");
            }
            Variable var = table.getVariable((String)before.getValue());
            table.updateVariable((String) before.getValue(), new Variable(after.getValue(), var.getType(), var.getAccessibility()));
            line.remove(currentTokenIndex + 1);
            line.remove(currentTokenIndex);
            line.remove(currentTokenIndex - 1);
        }

        System.out.println("test: " + table.getVariable("test").getValue());
    }

}
