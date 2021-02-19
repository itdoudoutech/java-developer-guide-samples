package com.doudou.questions;

/**
 * 给定一个二进制数组， 计算其中最大连续 1 的个数。
 * <p>
 * https://leetcode-cn.com/problems/max-consecutive-ones/
 */
public class Question_485 {

    public int findMaxConsecutiveOnes(int[] nums) {
        int max = 0, tmp = 0;
        for (int num : nums) {
            if (num == 1) {
                tmp += 1;
            } else {
                max = Math.max(max, tmp);
                tmp = 0;
            }
        }
        return Math.max(max, tmp);
    }
}
