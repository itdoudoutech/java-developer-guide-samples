package com.doudou.jvm.classloading;

public class SingletonTest {

    /**
     * result is: x = 1, y = 0
     */
    public static void main(String[] args) {
        System.out.printf("x = %d\n", Singleton.x);
        System.out.printf("y = %d\n", Singleton.y);
    }
}
