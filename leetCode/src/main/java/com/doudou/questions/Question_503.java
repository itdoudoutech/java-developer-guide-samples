package com.doudou.questions;

import java.util.Stack;

/**
 * 给定一个循环数组（最后一个元素的下一个元素是数组的第一个元素），输出每个元素的下一个更大元素。
 * 数字 x 的下一个更大的元素是按数组遍历顺序，这个数字之后的第一个比它更大的数，这意味着你应该循环地搜索它的下一个更大的数。
 * 如果不存在，则输出 -1。
 * <p>
 * https://leetcode-cn.com/problems/next-greater-element-ii
 */
public class Question_503 {

    public int[] nextGreaterElements(int[] nums) {
        Stack<Integer> stack = new Stack<>();
        int[] ans = new int[nums.length];
        for (int i = 2 * nums.length - 1; i >= 0; i--) {
            int num = nums[i % nums.length];
            while (!stack.isEmpty() && stack.peek() <= num) {
                stack.pop();
            }
            ans[i % nums.length] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(num);

        }
        return ans;
    }
}
