package org.gargiolang.compilation;

import org.gargiolang.compilation.parser.Parser;
import org.gargiolang.compilation.structures.symboltable.SymbolTable;
import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.ParsingException;
import org.gargiolang.tokenizer.LabelTable;
import org.gargiolang.tokenizer.tokens.TokenLine;

import java.util.LinkedList;

public class Compiler {

    private static final SymbolTable symbolTable = new SymbolTable();
    public static SymbolTable symbolTable() {
        return symbolTable;
    }

    public static void compile(LinkedList<TokenLine> tokens, LabelTable labelTable) throws ParsingException, UnrecognizedTypeException, UndeclaredVariableException {



        SyntaxTree syntaxTree = Parser.parse(tokens, labelTable, symbolTable);

    }

}
