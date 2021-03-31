package com.doudou.questions;


/**
 * 给你一个 正 整数 num ，输出它的补数。补数是对该数的二进制表示取反。
 * <p>
 * https://leetcode-cn.com/problems/number-complement/
 *
 * @see 1009 https://leetcode-cn.com/problems/complement-of-base-10-integer/
 * <p>
 * 异或操作
 * 10101
 * 11111
 */
public class Question_476 {

    public int findComplement(int num) {
        int n = 1;
        while (n < num) {
            n = (n << 1) + 1;
        }
        return n ^ num;
    }
}
