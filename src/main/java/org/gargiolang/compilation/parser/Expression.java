package org.gargiolang.compilation.parser;


import org.gargiolang.compilation.structures.trees.SyntaxNode;
import org.gargiolang.compilation.structures.trees.SyntaxTree;
import org.gargiolang.exception.evaluation.UndeclaredVariableException;
import org.gargiolang.exception.evaluation.UnrecognizedTypeException;
import org.gargiolang.exception.parsing.TokenConversionException;
import org.gargiolang.runtime.variable.Variable;
import org.gargiolang.tokenizer.tokens.Token;
import org.gargiolang.tokenizer.tokens.TokenLine;
import org.gargiolang.tokenizer.tokens.operators.ArithmeticOperator;
import org.gargiolang.tokenizer.tokens.operators.LogicalOperator;

public enum Expression {


    NULL(new Expression[0]),

    EVALUABLE(new Expression[0]),

    IDENTIFIER(new Expression[]{EVALUABLE}),

    NUMERIC(new Expression[]{EVALUABLE}),
    INTEGER(new Expression[]{NUMERIC}),
    FLOAT(new Expression[]{EVALUABLE}),

    BOOLEAN(new Expression[]{EVALUABLE}),

    ;

    private final Expression[] parents;

    Expression(Expression[] parents) {
        this.parents = parents;
    }


    public boolean is(Expression other) {
        if (other == this)
            return true;

        for (Expression parent : parents) {
            if (parent.is(other))
                return true;
        }

        return false;
    }


    public static SyntaxTree toSyntaxTree(TokenLine line) throws TokenConversionException, UnrecognizedTypeException, UndeclaredVariableException {
        SyntaxTree tree = new SyntaxTree();
        SyntaxNode node = toSyntaxNode(line.getFirst());
        tree.setRoot(node);

        for (Token token = line.getFirst().getNext(); token != null; token = token.getNext()) {
            // TODO: 08/12/20 here do parenthesis evaluation and modify priorities
            node.setRight(toSyntaxNode(token));
            node.getRight().setLeft(node);
            node = node.getRight();
        }

        return tree;
    }

    private static SyntaxNode toSyntaxNode(Token token) throws TokenConversionException, UnrecognizedTypeException, UndeclaredVariableException {
        SyntaxNode syntaxNode = null;

        switch (token.getType())
        {
            case ARITHMETIC_OPERATOR -> {
                switch ((ArithmeticOperator) token.getValue()) {
                    case ADD -> syntaxNode = new SyntaxNode(null, 5, NUMERIC, null, Operation.SUM);
                    case SUB -> syntaxNode = new SyntaxNode(null, 5, NUMERIC, null, Operation.SUBTRACTION);
                    case MUL -> syntaxNode = new SyntaxNode(null, 6, NUMERIC, null, Operation.MULTIPLICATION);
                    case DIV -> syntaxNode = new SyntaxNode(null, 6, NUMERIC, null, Operation.DIVISION);
                    case MOD -> syntaxNode = new SyntaxNode(null, 6, NUMERIC, null, Operation.MODULUS);
                    case POW -> syntaxNode = new SyntaxNode(null, 7, NUMERIC, null, Operation.POWER);
                    case INV -> syntaxNode = new SyntaxNode(null, 8, NUMERIC, null, Operation.INVERSE);
                    case INC -> syntaxNode = new SyntaxNode(null, 10, IDENTIFIER, null, Operation.INCREMENT);
                    case DEC -> syntaxNode = new SyntaxNode(null, 10, IDENTIFIER, null, Operation.DECREMENT);
                }
            }
            case LOGICAL_OPERATOR -> {
                switch ((LogicalOperator) token.getValue())
                {
                    case AND -> syntaxNode = new SyntaxNode(null, 2, BOOLEAN, null, Operation.AND);
                    case OR -> syntaxNode = new SyntaxNode(null, 2, BOOLEAN, null, Operation.OR);
                    case EQ -> syntaxNode = new SyntaxNode(null, 3, BOOLEAN, null, Operation.EQUALS_TO);
                    case NE -> syntaxNode = new SyntaxNode(null, 3, BOOLEAN, null, Operation.NOT_EQUALS_TO);
                    case GR -> syntaxNode = new SyntaxNode(null, 4, BOOLEAN, null, Operation.GREATER_THAN);
                    case LS -> syntaxNode = new SyntaxNode(null, 4, BOOLEAN, null, Operation.LESS_THAN);
                    case GRE -> syntaxNode = new SyntaxNode(null, 4, BOOLEAN, null, Operation.GREATER_OR_EQUAL);
                    case LSE -> syntaxNode = new SyntaxNode(null, 4, BOOLEAN, null, Operation.LESS_OR_EQUAL);
                    case NOT -> syntaxNode = new SyntaxNode(null, 8, BOOLEAN, null, Operation.NOT);
                }
            }
            case ASSIGNMENT_OPERATOR -> syntaxNode = new SyntaxNode(null, 1, IDENTIFIER, null, Operation.ASSIGNMENT);

            case NUM -> {
                switch (Variable.Type.extractVarType(token))
                {
                    case INT -> syntaxNode = new SyntaxNode(null, 0, INTEGER, token.getValue(), Operation.LITERAL);
                    case FLOAT -> syntaxNode = new SyntaxNode(null, 0, FLOAT, token.getValue(), Operation.LITERAL);
                }
            }

            // literal boolean (true, false)
            case BOOL -> syntaxNode = new SyntaxNode(null, 0, BOOLEAN, token.getValue(), Operation.LITERAL);

            case TYPE -> {
                switch ((Variable.Type) token.getValue())
                {
                    case INT -> syntaxNode = new SyntaxNode(null, 2, IDENTIFIER, null, Operation.DECLARATION_INT);
                    case BOOLEAN -> syntaxNode = new SyntaxNode(null, 2, IDENTIFIER, null, Operation.DECLARATION_BOOL);
                    case FLOAT -> syntaxNode = new SyntaxNode(null, 2, IDENTIFIER, null, Operation.DECLARATION_FLOAT);
                }
            }

            // value of an identifier is a pointer
            case TXT -> syntaxNode = new SyntaxNode(null, 0, IDENTIFIER, token.getValue(), Operation.LITERAL);
        }

        if (syntaxNode == null)
            throw new TokenConversionException("Cannot convert token " + token + " to a syntactical expression");

        return syntaxNode;
    }
}
