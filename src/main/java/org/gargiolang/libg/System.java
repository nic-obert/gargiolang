package org.gargiolang.libg;

import java.util.LinkedList;

public final class System {

    public static void print(LinkedList<Object> args) {
        java.lang.System.out.print((String) args.getFirst());
    }

    public static String test(LinkedList<Object> args){
        return args.get(0).toString() + args.get(1).toString();
    }

}
