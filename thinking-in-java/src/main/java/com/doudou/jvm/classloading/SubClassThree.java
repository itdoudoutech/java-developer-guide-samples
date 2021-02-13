package com.doudou.jvm.classloading;

public class SubClassThree extends SuperClass {

    public final static int x1 = SuperClass.a;
    public static int y1 = SuperClass.a;

    public final static int x2 = Singleton.y;
    public static int y2 = SuperClass.b;

    static {
        System.out.println("SubClassThree static block invoke");
    }

    {
        System.out.println("SubClassThree constructor block invoke");
    }

    public SubClassThree() {
        System.out.println("SubClassThree constructor method invoke");
    }
}
