package com.doudou.questions;


/**
 * 给定两个字符串 s 和 t，它们只包含小写字母。
 * 字符串 t 由字符串 s 随机重排，然后在随机位置添加一个字母。
 * 请找出在 t 中被添加的字母。
 * <p>
 * https://leetcode-cn.com/problems/find-the-difference/
 */
public class Question_339 {

    public char findTheDifference(String s, String t) {
        String x = s + t;
        int ans = 0;
        for (char c : x.toCharArray()) {
            ans ^= c;
        }
        return (char) ans;
    }

    public static void main(String[] args) {
        char c = new Question_339().findTheDifference("aaa", "aa");
        System.out.println(c);
    }
}
