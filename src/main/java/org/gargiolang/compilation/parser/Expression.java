package org.gargiolang.compilation.parser;

import org.gargiolang.compilation.structures.SyntaxNode;
import org.gargiolang.compilation.structures.SyntaxTree;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenLine;
import org.gargiolang.tokenizer.tokens.operators.ArithmeticOperator;
import org.gargiolang.tokenizer.tokens.operators.LogicalOperator;

public enum Expression {

    NUMERIC,
    IDENTIFIER,
    VARIABLE,
    EVALUABLE,
    BOOLEAN,
    NULL,
    ;


    public static SyntaxTree toSyntaxTree(TokenLine line) throws TokenConversionException {
        SyntaxTree tree = new SyntaxTree();
        SyntaxNode node = toSyntaxNode(line.getFirst());
        tree.setRoot(node);

        for (Token token = line.getFirst().getNext(); token != null; token = token.getNext()) {
            // TODO: 08/12/20 here do parenthesis evaluation and modify priorities
            node.setRight(toSyntaxNode(token));
            node = node.getRight();
        }

        return tree;
    }

    private static SyntaxNode toSyntaxNode(Token token) throws TokenConversionException {
        SyntaxNode syntaxNode = null;

        switch (token.getType())
        {
            case ARITHMETIC_OPERATOR -> {
                switch ((ArithmeticOperator) token.getValue()) {
                    case ADD -> syntaxNode = new SyntaxNode(null, token.getPriority(), EVALUABLE, null, Operation.SUM);
                    case SUB -> syntaxNode = new SyntaxNode(null, token.getPriority(), NUMERIC, null, Operation.SUBTRACTION);
                    case INV -> syntaxNode = new SyntaxNode(null, token.getPriority(), NUMERIC, null, Operation.INVERSE);
                    case MUL -> syntaxNode = new SyntaxNode(null, token.getPriority(), EVALUABLE, null, Operation.MULTIPLICATION);
                    case DIV -> syntaxNode = new SyntaxNode(null, token.getPriority(), NUMERIC, null, Operation.DIVISION);
                    case MOD -> syntaxNode = new SyntaxNode(null, token.getPriority(), NUMERIC, null, Operation.MODULUS);
                    case POW -> syntaxNode = new SyntaxNode(null, token.getPriority(), NUMERIC, null, Operation.POWER);
                    case INC -> syntaxNode = new SyntaxNode(null, token.getPriority(), VARIABLE, null, Operation.INCREMENT);
                    case DEC -> syntaxNode = new SyntaxNode(null, token.getPriority(), VARIABLE, null, Operation.DECREMENT);
                }
            }
            case LOGICAL_OPERATOR -> {
                switch ((LogicalOperator) token.getValue())
                {
                    case GR -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.GREATERTHAN);
                    case LS -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.LESSTHAN);
                    case GRE -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.GREATEROREQUAL);
                    case LSE -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.LESSOREQUAL);
                    case EQ -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.EQUALSTO);
                    case AND -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.AND);
                    case OR -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.OR);
                    case NOT -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.NOT);
                    case NE -> syntaxNode = new SyntaxNode(null, token.getPriority(), BOOLEAN, null, Operation.NOTEQUALSTO);
                }
            }
            case ASSIGNMENT_OPERATOR -> syntaxNode = new SyntaxNode(null, token.getPriority(), NULL, null, Operation.ASSIGNMENT);

            case NUM -> syntaxNode = new SyntaxNode(null, 0, NUMERIC, token.getValue(), Operation.LITERAL);

            case BOOL -> syntaxNode = new SyntaxNode(null, 0, BOOLEAN, token.getValue(), Operation.LITERAL);
        }

        if (syntaxNode == null)
            throw new TokenConversionException("Cannot convert token " + token + " to a syntactical expression");

        return syntaxNode;
    }
}
