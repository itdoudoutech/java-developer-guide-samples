package com.doudou.questions;

/**
 * 给定一个二维矩阵，计算其子矩形范围内元素的总和，该子矩阵的左上角为 (row1, col1) ，右下角为 (row2, col2)。
 * <p>
 * https://leetcode-cn.com/problems/range-sum-query-2d-immutable/
 * <p>
 * 你可以假设矩阵不可变。
 * 会多次调用 sumRegion 方法。
 * 你可以假设 row1 ≤ row2 且 col1 ≤ col2。
 */
public class Question_304 {

    private final int[][] matrix;

    public Question_304(int[][] matrix) {
        this.matrix = matrix;
        int row = matrix.length;
        if (row > 0) {
            int col = matrix[0].length;
            for (int i = 0; i < row; i++) {
                for (int j = 1; j < col; j++) {
                    matrix[i][j] += matrix[i][j - 1];
                }
            }
        }
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {
        int ans = 0;

        for (int i = row1; i <= row2; i++) {
            ans += matrix[i][col2] - (col1 == 0 ? 0 : matrix[i][col1 - 1]);
        }
        return ans;
    }
}