package com.doudou.questions;

import java.util.ArrayList;
import java.util.List;

/**
 * 给你一个 m 行 n 列的矩阵 matrix ，请按照 顺时针螺旋顺序 ，返回矩阵中的所有元素。
 * <p>
 * https://leetcode-cn.com/problems/palindrome-number
 */
public class Question_54 {

    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int rows = matrix.length, cols = matrix[0].length, total = rows * cols;
        int[][] visited = new int[rows][cols];
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int row = 0, col = 0, directionIndex = 0;
        for (int i = 0; i < total; i++) {
            result.add(matrix[row][col]);
            visited[row][col] = 1;
            int nextRow = row + directions[directionIndex][0];
            int nextCol = col + directions[directionIndex][1];
            if (nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols || visited[nextRow][nextCol] == 1) {
                directionIndex = (directionIndex + 1) % 4;
            }
            row += directions[directionIndex][0];
            col += directions[directionIndex][1];
        }
        return result;
    }

}
