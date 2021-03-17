package com.doudou.questions;

/**
 * 给你两个整数 left 和 right ，表示区间 [left, right] ，返回此区间内所有数字 按位与 的结果（包含 left 、right 端点）。
 * <p>
 * https://leetcode-cn.com/problems/bitwise-and-of-numbers-range/
 */
public class Question_201 {

    public int rangeBitwiseAnd(int left, int right) {
        while (right > left) {
            right = right & (right - 1);
        }
        return right;
    }
}
