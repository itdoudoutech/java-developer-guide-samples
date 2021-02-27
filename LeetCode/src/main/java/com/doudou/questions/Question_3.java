package com.doudou.questions;

import java.util.HashSet;
import java.util.Set;

/**
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * <p>
 * https://leetcode-cn.com/problems/longest-substring-without-repeating-characters/
 */
public class Question_3 {
    public int lengthOfLongestSubstring(String s) {
        int left = 0, right = 0, max = 0;
        Set<Character> values = new HashSet<>();
        while (right < s.length()) {
            while (right < s.length() && !values.contains(s.charAt(right))) {
                values.add(s.charAt(right++));
            }
            max = Math.max(max, right - left);
            values.remove(s.charAt(left++));
        }
        return max;
    }
}