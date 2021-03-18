package com.doudou.questions;

import java.util.Arrays;

/**
 * 给定一个整数数组 nums，其中恰好有两个元素只出现一次，其余所有元素均出现两次。 找出只出现一次的那两个元素。你可以按 任意顺序 返回答案。
 * <p>
 * 进阶：你的算法应该具有线性时间复杂度。你能否仅使用常数空间复杂度来实现？
 * <p>
 * https://leetcode-cn.com/problems/single-number-iii
 */
public class Question_260 {

    public static int[] singleNumber(int[] nums) {
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }
        int mask = xor & (-xor); // 取 xor 最低位的 1
        int[] ans = new int[2];
        for (int num : nums) {
            if ((mask & num) == 0) {
                ans[0] ^= num;
            } else {
                ans[1] ^= num;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 1, 3, 2, 5};
        int[] ans = singleNumber(nums);
        System.out.println(Arrays.toString(ans));
    }
}
