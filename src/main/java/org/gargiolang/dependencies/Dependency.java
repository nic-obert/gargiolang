package org.gargiolang.dependencies;

import java.net.URL;

public abstract class Dependency {

    private final URL fileURL;

    public Dependency(URL url){
        this.fileURL = url;
    }

}
