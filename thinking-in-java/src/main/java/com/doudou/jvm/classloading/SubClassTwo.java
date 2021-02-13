package com.doudou.jvm.classloading;

public class SubClassTwo extends SuperClass {

    static {
        System.out.println("SubClassTwo static block invoke");
    }

    {
        System.out.println("SubClassTwo constructor block invoke");
    }

    public SubClassTwo() {
        System.out.println("SubClassTwo constructor method invoke");
    }
}
