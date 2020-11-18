package org.gargiolang.dependencies.java;

import org.gargiolang.dependencies.Dependency;

import java.net.URL;
import java.util.jar.JarFile;

public abstract class JavaDependency extends Dependency {

    public JavaDependency(URL url){
        super(url);
    }



}
