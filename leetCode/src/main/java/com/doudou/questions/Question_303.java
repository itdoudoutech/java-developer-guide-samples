package com.doudou.questions;

/**
 * 给定一个整数数组  nums，求出数组从索引 i 到 j（i ≤ j）范围内元素的总和，包含 i、j 两点。
 * <p>
 * https://leetcode-cn.com/problems/range-sum-query-immutable/
 */
public class Question_303 {

    private final int[] sum;

    public Question_303(int[] nums) {
        sum = nums;
        for (int i = 1; i < nums.length; i++) {
            sum[i] += sum[i - 1];
        }
    }

    public int sumRange(int i, int j) {
        return i == 0 ? sum[j] : sum[j] - sum[i - 1];
    }

}