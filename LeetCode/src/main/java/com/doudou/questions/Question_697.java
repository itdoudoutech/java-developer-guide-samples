package com.doudou.questions;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个非空且只包含非负数的整数数组nums，数组的度的定义是指数组里任一元素出现频数的最大值
 * 你的任务是在 nums 中找到与 nums 拥有相同大小的度的最短连续子数组，返回其长度
 * <p>
 * https://leetcode-cn.com/problems/degree-of-an-array
 */
public class Question_697 {

    public int findShortestSubArray(int[] nums) {
        Map<Integer, int[]> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                map.get(nums[i])[0]++;
                map.get(nums[i])[2] = i;
            } else {
                map.put(nums[i], new int[]{1, i, i});
            }
        }

        int maxNum = 0, minLen = 0;
        for (int[] number : map.values()) {
            if (number[0] > maxNum) {
                maxNum = number[0];
                minLen = number[2] - number[1] + 1;
            } else if (number[0] == maxNum) {
                minLen = Math.min(minLen, number[2] - number[1] + 1);
            }
        }
        return minLen;
    }
}