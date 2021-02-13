package com.doudou.jvm.classloading;

public class SubClassFour {

    public final static int a = 10;
    public static int b = 20;
    private int d = 40;

    static {
        System.out.printf("SubClass static block [a = %d, b = %d]\n", a, b);
        b = 100;
        c = 100;
    }

    {
        System.out.printf("SubClass constructor block [a = %d, b = %d, c = %d, d = %d]\n", a, b, c, d);
    }

    public static int c = 30;

    public SubClassFour() {
        System.out.printf("SubClass constructor with no args [a = %d, b = %d, c = %d, d = %d]\n", a, b, c, d);
    }

    public SubClassFour(int d) {
        this.d = d;
        System.out.printf("SubClass constructor with one arg [a = %d, b = %d, c = %d, d = %d]\n", a, b, c, d);
    }


}
