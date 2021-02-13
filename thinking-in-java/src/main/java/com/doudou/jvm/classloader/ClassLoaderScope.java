package com.doudou.jvm.classloader;

public class ClassLoaderScope {

    private static final String SPLIT = ":";

    public static void main(String[] args) {
        final String bootPath = System.getProperty("sun.boot.class.path");
        System.out.println(bootPath.replaceAll(SPLIT, System.lineSeparator()));
        System.out.println("----------------------------");

        String extPath = System.getProperty("java.ext.dirs");
        System.out.println(extPath.replaceAll(SPLIT, System.lineSeparator()));
        System.out.println("----------------------------");

        final String appPath = System.getProperty("java.class.path");
        System.out.println(appPath.replaceAll(SPLIT, System.lineSeparator()));
    }
}
