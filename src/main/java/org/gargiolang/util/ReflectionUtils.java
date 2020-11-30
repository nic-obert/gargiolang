package org.gargiolang.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

public final class ReflectionUtils {

    public static Class<?> loadClass(String path) throws ReflectiveOperationException{
        return Class.forName(path);
    }

    public static Object invokeSystemCall(String call, LinkedList<Object> args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = org.gargiolang.libg.System.class.getDeclaredMethod(call, LinkedList.class);
        return method.invoke(null, args);
    }

    public static Object instantiateClass(Class<?> cls, Object[] constructor)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?>[] classes = new Class[constructor.length];

        for(int i = 0; i < constructor.length; i++){
            classes[i] = constructor.getClass();
        }

        Constructor<?> constr = cls.getConstructor(classes);

        return constr.newInstance(constructor);
    }

    public static Object getField(Class<?> cls, String fieldName) throws NoSuchFieldException {
        return cls.getField(fieldName);
    }

    public static Method getMethod(Class<?> cls, String name, Object[] args) throws NoSuchMethodException {
        Class<?>[] classes = new Class[args.length];

        for(int i = 0; i < args.length; i++){
            classes[i] = args.getClass();
        }

        return cls.getDeclaredMethod(name, classes);
    }

    public static Object invokeField(Method method, Object instance, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(instance, args);
    }

}
