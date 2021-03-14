package com.doudou.questions;

/**
 * 给定一个整数，编写一个函数来判断它是否是 2 的幂次方。
 * <p>
 * https://leetcode-cn.com/problems/power-of-two/
 */
public class Question_231 {

    public boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

}
