package com.doudou.questions;


/**
 * 给定一个正整数，检查它的二进制表示是否总是 0、1 交替出现：换句话说，就是二进制表示中相邻两位的数字永不相同。
 * <p>
 * https://leetcode-cn.com/problems/binary-number-with-alternating-bits/
 */
public class Question_693 {
    public boolean hasAlternatingBits(int n) {
        int tmp = n ^ (n >> 1);
        return (tmp & (tmp + 1)) == 0;
    }
}
