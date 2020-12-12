package org.gargiolang.compilation.parser;

import org.gargiolang.compilation.structures.symboltable.SymbolTable;
import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UndefinedLabelException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.*;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Parser {

    public static SyntaxTree parse(LinkedList<TokenLine> tokens, LabelTable labelTable, SymbolTable symbolTable) throws ParsingException, UnrecognizedTypeException, UndeclaredVariableException {

        // get the entry point to the program
        int entryPoint;
        try {
            entryPoint = labelTable.getLabel("main");
        } catch (UndefinedLabelException e) {throw new NoEntryPointException("An entry point to the program must be specified by @main label");}


        // create the root of the syntax tree (starting from the program's entry point)

        SyntaxTree syntaxTree = new SyntaxTree();

        TokenLine line = tokens.get(entryPoint);
        SyntaxTree tree = SyntaxTree.fromTokenLine(line);


        System.out.println(tree);
        System.out.println("\n\n");

        tree.parse();

        System.out.println(tree);




        return syntaxTree;

    }

}
