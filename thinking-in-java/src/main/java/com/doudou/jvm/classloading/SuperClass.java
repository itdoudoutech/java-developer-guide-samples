package com.doudou.jvm.classloading;

public class SuperClass {

    public final static int a = 10;

    public static int b = 20;

    static {
        System.out.println("SuperClass static block invoke");
    }

    {
        System.out.println("SuperClass constructor block invoke");
    }

    public SuperClass() {
        System.out.println("SuperClass constructor method invoke");
    }
}
