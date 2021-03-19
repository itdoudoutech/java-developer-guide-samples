package com.doudou.questions;


/**
 * 给定一个整数，写一个函数来判断它是否是 4 的幂次方。如果是，返回 true ；否则，返回 false 。
 * 整数 n 是 4 的幂次方需满足：存在整数 x 使得 n == 4x
 * <p>
 * https://leetcode-cn.com/problems/power-of-four
 */
public class Question_342 {

    // 正数 二进制位中只包含一个 1，且该 1 的位置在 0、2、4、6 个二进制位上
    // (0xAAAAAAAA & n) == 0 确保 1、3、5、7... 位上没有 1
    public boolean isPowerOfFour(int n) {
        return n > 0 && ((0xAAAAAAAA & n) == 0) && (n & (n - 1)) == 0;
    }
}
