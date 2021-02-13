package com.doudou.jvm.classloader;

public class CustomerClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader classLoader = CustomerClassLoader.class.getClassLoader();
        Class<?> aClass = classLoader.loadClass("com.doudou.jvm.classloader.Person");
        System.out.println(aClass.getSimpleName());
    }
}
