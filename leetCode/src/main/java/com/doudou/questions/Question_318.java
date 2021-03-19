package com.doudou.questions;


/**
 * 给定一个字符串数组 words，找到 length(word[i]) * length(word[j]) 的最大值，并且这两个单词不含有公共字母。你可以认为每个单词只包含小写字母。如果不存在这样的两个单词，返回 0。
 * <p>
 * https://leetcode-cn.com/problems/maximum-product-of-word-lengths
 */
public class Question_318 {

    public int maxProduct(String[] words) {
        int ans = 0;
        int[] nums = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            nums[i] = converter(words[i]);
        }

        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if ((nums[i] & nums[j]) != 0) {
                    ans = Math.max(ans, words[i].length() * words[j].length());
                }
            }
        }

        return ans;
    }

    private int converter(String word) {
        int ans = 0;
        for (char c : word.toCharArray()) {
            ans |= 1 << (c - 'a');
        }
        return ans;
    }
}
