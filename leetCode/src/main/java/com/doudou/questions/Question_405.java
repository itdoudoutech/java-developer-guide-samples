package com.doudou.questions;


import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数，编写一个算法将这个数转换为十六进制数。对于负整数，我们通常使用补码运算方法
 * <p>
 * https://leetcode-cn.com/problems/convert-a-number-to-hexadecimal/
 */
public class Question_405 {

    public String toHex(int num) {
        if (num == 0) {
            return "0";
        }
        String hex = "0123456789abcdef";
        StringBuilder ans = new StringBuilder();
        while (num != 0) {
            ans.append(hex.charAt(num & 0xf));
            num = num >>> 4;
        }
        return ans.reverse().toString();
    }

    public static void main(String[] args) {
        for (int i = -1; i < 1000; i++) {
            String x = Integer.toHexString(i);
            String y = new Question_405().toHex(i);
            if (!x.equals(y)) {
                System.out.println(i + " : " + x + " : " + y);
            }
        }
    }
}
