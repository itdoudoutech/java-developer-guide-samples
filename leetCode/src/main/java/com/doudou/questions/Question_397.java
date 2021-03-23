package com.doudou.questions;


/**
 * 给定一个正整数 n ，你可以做如下操作：
 * 如果 n 是偶数，则用 n / 2 替换 n
 * 如果 n 是奇数，则可以用 n + 1 或 n - 1 替换 n
 * n 变为 1 所需的最小替换次数是多少
 *
 * <p>
 * https://leetcode-cn.com/problems/integer-replacement
 */
public class Question_397 {

    public int integerReplacement(int x) {
        long n = x;
        int count = 0;
        while (n != 1) {
            if ((n & 1) == 0) {
                n = n >> 1;
            } else {
                n += (n == 3 || (n & 2) == 0) ? -1 : 1;
            }
            count++;
        }
        return count;
    }

    public static void main(String[] args) {
        int c = new Question_397().integerReplacement(Integer.MIN_VALUE);
        System.out.println(c);
    }
}
