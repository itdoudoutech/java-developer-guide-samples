package com.doudou.questions;


/**
 * 不使用运算符 + 和 - ，计算两整数 a 、b 之和。
 * <p>
 * https://leetcode-cn.com/problems/sum-of-two-integers/
 */
public class Question_371 {
    public int getSum(int a, int b) {
        while (b != 0) {
            int temp = a ^ b;
            b = (a & b) << 1;
            a = temp;
        }
        return a;
    }
}
