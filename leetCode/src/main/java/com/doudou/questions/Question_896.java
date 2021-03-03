package com.doudou.questions;

/**
 * 如果数组是单调递增或单调递减的，那么它是单调的。
 * 如果对于所有 i <= j，A[i] <= A[j]，那么数组 A 是单调递增的。 如果对于所有 i <= j，A[i]> = A[j]，那么数组 A 是单调递减的。
 * 当给定的数组 A 是单调数组时返回 true，否则返回 false。
 * <p>
 * https://leetcode-cn.com/problems/monotonic-array
 */
public class Question_896 {
    public boolean isMonotonic(int[] A) {
        boolean flag = A[A.length - 1] > A[0];
        for (int i = 1; i < A.length; i++) {
            if ((A[i] != A[i - 1]) && flag ^ (A[i] > A[i - 1])) {
                return false;
            }
        }
        return true;
    }
}