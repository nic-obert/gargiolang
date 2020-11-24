package org.gargiolang.runtime.variable.types;


import org.gargiolang.lang.Token;

abstract public class Type {

    /*
    It doesn't make sense to have static methods in an abstract class, as, because of oop, you cannot override them, nor
    can you force a subclass to implement it. This class should be used either for default methods, or abstract methods
    to be overridden, but not static methods.
     */
}
