package com.doudou.jvm.classloading;

public class Singleton {

    private static Singleton singleTon = new Singleton();

    public static int x;

    public static int y = 10;

    private Singleton() {
        x++;
        y++;
        System.out.printf("Singleton constructor method x = %d, y =  %d\n", x, y);
    }

    static {
        System.out.printf("Singleton static block x = %d, y =  %d\n", x, y);
    }

    {
        y++;
        System.out.printf("Singleton constructor block x = %d, y =  %d\n", x, y);
    }
}
