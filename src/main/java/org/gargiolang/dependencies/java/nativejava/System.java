package org.gargiolang.dependencies.java.nativejava;

import org.gargiolang.dependencies.java.JavaDependency;
import org.gargiolang.dependencies.java.NativeJavaDependency;
import org.gargiolang.dependencies.java.ReflectionJavaDependency;

/**
 * I will implement a system through which you can simply use the words "#include org.gargiolang.java.nativejava.System"
 * (will be shortened), and it will instantiate the class. when you then call a function in the class it will attempt to
 * run the function with the provided args
 */
public class System extends NativeJavaDependency {

    public void print(Object message){
        java.lang.System.out.println(message.toString());
    }

    public long currentTime(){
        return java.lang.System.currentTimeMillis();
    }

    public long currentNanos(){
        return java.lang.System.nanoTime();
    }

}
