package org.gargiolang;

import org.gargiolang.runtime.Runtime;
import org.gargiolang.lang.exception.GargioniException;

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
