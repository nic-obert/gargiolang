package org.gargiolang.compilation;

import org.gargiolang.compilation.parser.Parser;
import org.gargiolang.compilation.structures.SyntaxTree;
import org.gargiolang.exception.parsing.NoEntryPointException;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Compiler {

    public static void compile(LinkedList<TokenLine> tokens, LabelTable labelTable) throws TokenConversionException, NoEntryPointException {

        SyntaxTree syntaxTree = Parser.parse(tokens, labelTable);

    }

}
