package com.doudou.jvm.bytecode;

public class ByteCode {

    public static void main(String[] args) {
        int x = 15;
        int y = 5;

        int a = x + y;
        int b = x - y;
        int c = x * y;
        int d = x / y;

        if (d == 3) {
            System.out.println("d == 3");
        }

        for (int i = 0; i < 10; i++) {
            x++;
        }

        System.out.printf("x = %d, y = %d, a = %d, b = %d, c = %d, d = %d", x, y, a, b, c, d);
    }


}
