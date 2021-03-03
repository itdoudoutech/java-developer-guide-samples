package com.doudou.questions;

/**
 * 在MATLAB中，有一个非常有用的函数 reshape，它可以将一个矩阵重塑为另一个大小不同的新矩阵，但保留其原始数据。
 * 给出一个由二维数组表示的矩阵，以及两个正整数r和c，分别表示想要的重构的矩阵的行数和列数。
 * 重构后的矩阵需要将原始矩阵的所有元素以相同的行遍历顺序填充。
 * 如果具有给定参数的reshape操作是可行且合理的，则输出新的重塑矩阵；否则，输出原始矩阵。
 * <p>
 * 注意：
 * 1、给定矩阵的宽和高范围在 [1, 100]。
 * 2、给定的 r 和 c 都是正数。
 * <p>
 * https://leetcode-cn.com/problems/reshape-the-matrix
 */
public class Question_566 {

    public int[][] matrixReshape(int[][] nums, int r, int c) {
        int row = nums.length;
        int col = nums[0].length;
        if (r * c != row * col) {
            return nums;
        }

        int[][] ans = new int[r][c];
        for (int i = 0; i < row * col; i++) {
            ans[i / c][i % c] = nums[i / col][i % col];
        }
        return ans;
    }

}
