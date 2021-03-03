package com.doudou.questions;

/**
 * 给定一个非负整数 num。对于 0 ≤ i ≤ num 范围中的每个数字 i ，计算其二进制数中的 1 的数目并将它们作为数组返回。
 * <p>
 * https://leetcode-cn.com/problems/counting-bits/
 */
public class Question_338 {

    public int[] countBits(int num) {
        int[] ans = new int[num + 1];
        for (int i = 1; i < num + 1; i++) {
            ans[i] = ans[i >> 1] + (i & 2);
        }
        return ans;
    }
}