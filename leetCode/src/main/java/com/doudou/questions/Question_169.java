package com.doudou.questions;

/**
 * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 * <p>
 * https://leetcode-cn.com/problems/majority-element
 */
public class Question_169 {

    public int majorityElement(int[] nums) {
        int count = 0, major = nums[0];
        for (int num : nums) {
            if (count == 0) {
                major = num;
            }
            count += (num == major ? 1 : -1);
        }
        return major;
    }
}
