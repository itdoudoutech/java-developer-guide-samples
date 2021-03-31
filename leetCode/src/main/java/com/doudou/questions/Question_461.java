package com.doudou.questions;


/**
 * 两个整数之间的汉明距离指的是这两个数字对应二进制位不同的位置的数目。
 * 给出两个整数 x 和 y，计算它们之间的汉明距离。
 * <p>
 * https://leetcode-cn.com/problems/hamming-distance/
 */
public class Question_461 {

    public int hammingDistance(int x, int y) {
        int count = 0;
        int z = x ^ y;
        while (z != 0) {
            z = z & (z - 1);
            count++;
        }
        return count;
    }
}
