package com.doudou.questions;


/**
 * 给定两个整数 L 和 R，找到闭区间 [L, R] 范围内，计算置位位数为质数的整数个数。
 * （注意，计算置位代表二进制表示中1的个数。例如21的二进制表示10101有 3 个计算置位。还有，1 不是质数。）
 * <p>
 * L, R 是 L <= R 且在 [1, 10^6] 中的整数。
 * <p>
 * https://leetcode-cn.com/problems/prime-number-of-set-bits-in-binary-representation
 */
public class Question_762 {
    public int countPrimeSetBits(int L, int R) {
        // 2,3,5,7,11,13,17,19
        // 能用数组就不用集合（set）
        int[] ans = new int[]{0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1};
        int count = 0;
        for (int i = L; i < R + 1; i++) {
            count += ans[Integer.bitCount(i)];
        }
        return count;
    }
}
