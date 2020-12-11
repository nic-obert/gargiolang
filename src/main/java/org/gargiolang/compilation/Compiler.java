package org.gargiolang.compilation;

import org.gargiolang.compilation.parser.Parser;
import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Compiler {

    public static void compile(LinkedList<TokenLine> tokens, LabelTable labelTable) throws ParsingException {

        SyntaxTree syntaxTree = Parser.parse(tokens, labelTable);

    }

}
