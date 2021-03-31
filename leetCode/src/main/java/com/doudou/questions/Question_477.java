package com.doudou.questions;


/**
 * 两个整数的 汉明距离 指的是这两个数字的二进制数对应位不同的数量。
 * 计算一个数组中，任意两个数之间汉明距离的总和。
 * <p>
 * https://leetcode-cn.com/problems/total-hamming-distance/
 */
public class Question_477 {

    public int totalHammingDistance(int[] nums) {
        int ans = 0, n = nums.length;
        for (int i = 0; i < 32; i++) {
            int oneCount = 0;
            for (int num : nums) {
                oneCount += (num >> i) & 1;
            }
            ans += oneCount * (n - oneCount);
        }
        return ans;
    }
}
