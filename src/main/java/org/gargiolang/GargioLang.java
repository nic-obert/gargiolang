package org.gargiolang;

import org.gargiolang.lang.Parser;
import org.gargiolang.lang.Runtime;
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

        Runtime runtime = new Runtime();
        runtime.runFile(args[0]);
    }

}
