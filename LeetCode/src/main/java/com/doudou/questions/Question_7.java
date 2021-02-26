package com.doudou.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给你一个 32 位的有符号整数 x ，返回 x 中每位上的数字反转后的结果。
 * 如果反转后整数超过 32 位的有符号整数的范围 [−231, 231− 1] ，就返回 0。
 * 假设环境不允许存储 64 位整数（有符号或无符号）。
 *
 * <p>
 * https://leetcode-cn.com/problems/reverse-integer
 */
public class Question_7 {
    public int reverse(int x) {
        int ans = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if (ans > Integer.MAX_VALUE / 10 || (ans == Integer.MAX_VALUE / 10 && pop > 7)) {
                return 0;
            }
            if (ans < Integer.MIN_VALUE / 10 || (ans == Integer.MIN_VALUE / 10 && pop < -8)) {
                return 0;
            }
            ans = ans * 10 + pop;
        }
        return ans;
    }

    public static void main(String[] args) {
        Question_7 question = new Question_7();
        List<Integer> list = new ArrayList<>(Arrays.asList(1214748364, -1214748364, 120, -120, -2147483648));
        list.forEach(p -> System.out.println(question.reverse(p)));
    }
}