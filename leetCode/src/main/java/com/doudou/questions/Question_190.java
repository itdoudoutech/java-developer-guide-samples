package com.doudou.questions;

/**
 * 颠倒给定的 32 位无符号整数的二进制位。
 * <p>
 * https://leetcode-cn.com/problems/reverse-bits/
 */
public class Question_190 {

    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        n = (n >>> 16) | (n << 16);
        n = ((n & 0xFF00FF00) >>> 8) | ((n & 0x00FF00FF) << 8);
        n = ((n & 0xF0F0F0F0) >>> 4) | ((n & 0x0F0F0F0F) << 4);
        n = ((n & 0xCCCCCCCC) >>> 2) | ((n & 0x33333333) << 2);
        n = ((n & 0xAAAAAAAA) >>> 1) | ((n & 0x55555555) << 1);
        return n;
    }
}
