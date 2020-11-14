package org.gargiolang.lang;

import java.util.LinkedList;
import java.util.List;

public class Parser {

    private final String[] statements;
    private final LinkedList<Token> tokens;

    public Parser(String[] statements){
        this.statements = statements;

        this.tokens = parseTokens();
    }

    private LinkedList<Token> parseTokens(){
        final LinkedList<Token> tokens = new LinkedList<>();

        for(String statement : statements){
            final String[] split = statement.split("");


            for(int i = 0; i < split.length; i++){
                String c = split[i];


            }
        }

        return tokens;
    }
}
