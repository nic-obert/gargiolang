package org.gargiolang.compilation.parser;

import org.gargiolang.compilation.structures.SyntaxTree;
import org.gargiolang.exception.evaluation.UndefinedLabelException;
import org.gargiolang.exception.parsing.NoEntryPointException;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Parser {

    public static SyntaxTree parse(LinkedList<TokenLine> tokens, LabelTable labelTable) throws NoEntryPointException, TokenConversionException {

        // get the entry point to the program
        int entryPoint;
        try {
            entryPoint = labelTable.getLabel("main");
        } catch (UndefinedLabelException e) {throw new NoEntryPointException("An entry point to the program must be specified by @main label");}


        // create the root of the syntax tree (starting from the program's entry point)

        SyntaxTree syntaxTree = new SyntaxTree();

        TokenLine line = tokens.get(entryPoint + 1);
        SyntaxTree tree = SyntaxTree.fromTokenLine(line);




        return syntaxTree;

    }

}
