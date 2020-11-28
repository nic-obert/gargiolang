package org.gargiolang.preprocessing.dependencies.java.nativejava;

import org.gargiolang.preprocessing.dependencies.java.NativeJavaDependency;
import org.gargiolang.environment.Environment;

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

    public String getProperty(String name) {
        return Environment.getInstance().getProperty(name);
    }

    public String getEnv(String name){
        return Environment.getInstance().getEnv(name);
    }

}