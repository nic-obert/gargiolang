package org.gargiolang;

import org.gargiolang.lang.Parser;
import org.gargiolang.lang.exception.GargioniException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public class GargioLang {

    //C:\Users\konra\Desktop\Programming\gargiolang\test.gl

    public static void main(String[] args) throws Exception{
        if(args == null || args.length != 1){
            throw new GargioniException("Specify a file.");
        }

        final File file = new File(args[0]);

        if(!file.exists()){
            throw new GargioniException("The specified file couldn't be found (" + args[0] + ").");
        }

        final byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append((char) aByte);
        }

        final LinkedList<String> tempStatements = new LinkedList<>();
        for(String s : builder.toString().replaceAll("\n", "").split(";")){
            if(!s.startsWith("//") && !s.isEmpty()){
                tempStatements.add(s);
            }
        }
        final String[] statements = tempStatements.toArray(new String[0]);
        for (String statement : statements) {
            System.out.println(statement);
        }

        Parser parser = new Parser(statements);
        parser.parseTokens();
        System.out.println(parser.getTokens());

    }

}
