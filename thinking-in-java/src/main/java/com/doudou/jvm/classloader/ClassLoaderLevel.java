package com.doudou.jvm.classloader;

public class ClassLoaderLevel {

    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println(ClassLoaderLevel.class.getClassLoader());

        System.out.println("*****");

        System.out.println(ClassLoaderLevel.class.getClassLoader()); // AppClassLoader
        System.out.println(ClassLoaderLevel.class.getClassLoader().getClass().getClassLoader()); // BootstrapClassLoader
        System.out.println(ClassLoaderLevel.class.getClassLoader().getParent()); // ExtClassLoader
        System.out.println(ClassLoaderLevel.class.getClassLoader().getParent().getClass().getClassLoader()); // BootstrapClassLoader
        System.out.println(ClassLoaderLevel.class.getClassLoader().getParent().getParent()); // BootstrapClassLoader

    }

}
