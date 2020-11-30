package org.gargiolang.libg;

import org.gargiolang.environment.Environment;

import java.util.LinkedList;

public final class System {

    public static void print(LinkedList<Object> args) {
        java.lang.System.out.print((String) args.getFirst());
    }

    public static long currentTime(LinkedList<Object> args){
        return java.lang.System.currentTimeMillis();
    }

    public static long currentNanos(LinkedList<Object> args){
        return java.lang.System.nanoTime();
    }

    public static String getProperty(LinkedList<Object> args) {
        return Environment.getInstance().getProperty((String) args.getFirst());
    }

    public static String getEnv(LinkedList<Object> args){
        return Environment.getInstance().getEnv((String) args.getFirst());
    }

}
