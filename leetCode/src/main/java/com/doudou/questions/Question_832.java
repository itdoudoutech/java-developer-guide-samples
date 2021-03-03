package com.doudou.questions;

/**
 * 给定一个二进制矩阵 A，我们想先水平翻转图像，然后反转图像并返回结果。
 * 水平翻转图片就是将图片的每一行都进行翻转，即逆序。例如，水平翻转 [1, 1, 0] 的结果是 [0, 1, 1]。
 * 反转图片的意思是图片中的 0 全部被 1 替换，1 全部被 0 替换。例如，反转 [0, 1, 1] 的结果是 [1, 0, 0]。
 * <p>
 * https://leetcode-cn.com/problems/flipping-an-image
 * <p>
 * 1 <= A.length = A[0].length <= 20
 * 0 <= A[i][j] <= 1
 */
public class Question_832 {
    public int[][] flipAndInvertImage(int[][] A) {
        int len = A.length;
        for (int i = 0; i < len; i++) {
            int m = 0, n = len - 1;
            while (m <= n) {
                int tmp = A[i][m] ^ 1;
                A[i][m++] = A[i][n] ^ 1;
                A[i][n--] = tmp;
            }
        }
        return A;
    }
}