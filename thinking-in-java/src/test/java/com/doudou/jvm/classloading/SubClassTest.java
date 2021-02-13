package com.doudou.jvm.classloading;

import org.junit.Test;

public class SubClassTest {

    @Test
    public void testInvokeOrderWithNoSuperClass() {
        new SubClassOne();
        System.out.println("*****");
        new SubClassOne();
    }


    @Test
    public void testInvokeOrderWithSuperClass() {
        new SubClassTwo();
        System.out.println("*****");
        new SubClassTwo();
    }

    @Test
    public void testSuperFinalField() {
        System.out.println(SubClassThree.a);
    }

    @Test
    public void testSuperStaticField() {
        System.out.println(SubClassThree.b);
    }

    @Test
    public void testSubFinalFieldOne() {
        System.out.println(SubClassThree.x1);
    }

    @Test
    public void testSubFinalFieldTwo() {
        System.out.println(SubClassThree.x2);
    }

    @Test
    public void testSubStaticFieldOne() {
        System.out.println(SubClassThree.y1);
    }

    @Test
    public void testSubStaticFieldTwo() {
        System.out.println(SubClassThree.y2);
    }
}
