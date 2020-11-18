package org.gargiolang.dependencies.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public abstract class ReflectionJavaDependency extends JavaDependency {
    public ReflectionJavaDependency(URL url) {
        super(url);
    }

}
