package com.doudou.questions;

import java.util.*;

/**
 * 所有 DNA 都由一系列缩写为 'A'，'C'，'G' 和 'T' 的核苷酸组成，例如："ACGAATTCCG"。在研究 DNA 时，识别 DNA 中的重复序列有时会对研究非常有帮助。
 * 编写一个函数来找出所有目标子串，目标子串的长度为 10，且在 DNA 字符串 s 中出现次数超过一次。
 * <p>
 * https://leetcode-cn.com/problems/repeated-dna-sequences
 */
public class Question_187 {

    public static void main(String[] args) {
        System.out.println(findRepeatedDnaSequences("A"));
    }

    public static List<String> findRepeatedDnaSequences(String s) {
        // return findRepeatedDnaSequencesByMap(s);
        if(s.isEmpty() || s.length() < 10){
            return new ArrayList<>();
        }
        Set<String> result = new HashSet<>();
        Set<Integer> set = new HashSet<>();
        Map<Character, Integer> map = new HashMap<>();
        map.put('A', 0);
        map.put('C', 1);
        map.put('G', 2);
        map.put('T', 3);
        int[] values = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            values[i] = map.get(s.charAt(i));
        }

        int key = 0, len = 10;
        for (int i = 0; i < len; i++) {
            key = key << 2;
            key = key | values[i];
        }
        set.add(key);

        for (int i = len; i < s.length(); i++) {
            key = key << 2;
            key = key | values[i];
            key = key & 0xFFFFF; // 十个字母，每个字母需要 2 bit，因此只需要保留低位 20 位即可

            if (set.contains(key)) {
                result.add(s.substring(i - len + 1, i + 1));
            }
            set.add(key);
        }

        return new ArrayList<>(result);
    }

    private static List<String> findRepeatedDnaSequencesByMap(String s) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < s.length() - 9; i++) {
            String subString = s.substring(i, i + 10);
            map.put(subString, map.getOrDefault(subString, 0) + 1);
        }
        map.forEach((k, v) -> {
            if (v > 1) {
                result.add(k);
            }
        });
        return result;
    }
}
