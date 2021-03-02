package com.doudou.questions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 将一个给定字符串 s 根据给定的行数 numRows ，以从上往下、从左到右进行 Z 字形排列。
 * <p>
 * https://leetcode-cn.com/problems/zigzag-conversion/
 */
public class Question_6 {

    public String convert(String s, int numRows) {
        if (numRows == 1) {
            return s;
        }

        List<StringBuilder> list = IntStream.range(0, numRows).boxed().map(StringBuilder::new).collect(Collectors.toList());
        int currRow = 0;
        boolean direction = false;
        for (char c : s.toCharArray()) {
            list.get(currRow).append(c);
            if (currRow == 0 || currRow == numRows - 1) {
                direction = !direction;
            }
            currRow += direction ? 1 : -1;
        }

        return list.stream().map(StringBuilder::toString).collect(Collectors.joining());
    }
}