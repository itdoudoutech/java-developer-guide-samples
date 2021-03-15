package com.doudou.questions;

import java.util.Arrays;

/**
 * 给你一个正整数 n ，生成一个包含 1 到 n2 所有元素，且元素按顺时针顺序螺旋排列的 n x n 正方形矩阵 matrix 。
 * <p>
 * https://leetcode-cn.com/problems/spiral-matrix-ii/
 */
public class Question_59 {

    public int[][] generateMatrix(int n) {
        int[][] ans = new int[n][n];
        int[][] visited = new int[n][n];
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int row = 0, col = 0, directionIndex = 0, total = n * n;
        for (int i = 0; i < total; i++) {
            ans[row][col] = i + 1;
            visited[row][col] = 1;
            int nextRow = row + directions[directionIndex][0];
            int nextCol = col + directions[directionIndex][1];
            if (nextRow < 0 || nextRow >= n || nextCol < 0 || nextCol >= n || visited[nextRow][nextCol] == 1) {
                directionIndex = (directionIndex + 1) % 4;
            }
            row += directions[directionIndex][0];
            col += directions[directionIndex][1];
        }
        return ans;
    }

    public static void main(String[] args) {
        for (int n = 1; n < 6; n++) {
            int[][] ans = new Question_59().generateMatrix(n);
            for (int i = 0; i < n; i++) {
                System.out.println(Arrays.toString(ans[i]));
            }
            System.out.println("#####");
        }
    }
}
