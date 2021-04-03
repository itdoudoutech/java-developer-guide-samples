package com.doudou.questions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 给你一个整数数组 arr。请你将数组中的元素按照其二进制表示中数字 1 的数目升序排序。
 * 如果存在多个数字二进制中 1 的数目相同，则必须将它们按照数值大小升序排列。
 * 请你返回排序后的数组。
 *
 * <p>
 * https://leetcode-cn.com/problems/sort-integers-by-the-number-of-1-bits
 */
public class Question_1356 {
    public int[] sortByBits(int[] arr) {
        List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
        int[] bit = new int[10001];
        for (int i = 1; i <= 10000; ++i) {
            bit[i] = bit[i >> 1] + (i & 1);
        }
        list.sort((x, y) -> bit[x] != bit[y] ? bit[x] - bit[y] : x - y);
        return list.stream().mapToInt(Integer::valueOf).toArray();
    }
}
