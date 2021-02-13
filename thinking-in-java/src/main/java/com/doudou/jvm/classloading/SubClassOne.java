package com.doudou.jvm.classloading;

public class SubClassOne {

    static {
        System.out.println("SubClassOne static block invoke");
    }

    {
        System.out.println("SubClassOne constructor block invoke");
    }

    public SubClassOne() {
        System.out.println("SubClassOne constructor method invoke");
    }
}
