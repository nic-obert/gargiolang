package org.gargiolang.compilation.parser;

import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Parser {

    public static SyntaxTree parse(LinkedList<TokenLine> tokens, LabelTable labelTable) throws ParsingException, UnrecognizedTypeException, UndeclaredVariableException {


        SyntaxTree syntaxTree = new SyntaxTree();

        for (TokenLine line : tokens) {
            SyntaxTree tree = SyntaxTree.fromTokenLine(line);
            tree.parse();
            syntaxTree.join(tree);
        }

        System.out.println(syntaxTree);

        return syntaxTree;

    }

}
