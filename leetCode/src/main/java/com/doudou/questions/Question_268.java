package com.doudou.questions;

/**
 * 给定一个包含 [0, n] 中 n 个数的数组 nums ，找出 [0, n] 这个范围内没有出现在数组中的那个数。
 * <p>
 * 你能否实现线性时间复杂度、仅使用额外常数空间的算法解决此问题?
 * <p>
 * https://leetcode-cn.com/problems/missing-number/
 */
public class Question_268 {

    public int missingNumber(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += (i + 1);
            sum -= nums[i];
        }
        return sum;
    }
}
