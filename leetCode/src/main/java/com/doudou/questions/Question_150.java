package com.doudou.questions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * 根据 逆波兰表示法，求表达式的值。
 * 有效的算符包括 +、-、*、/ 。每个运算对象可以是整数，也可以是另一个逆波兰表达式。
 * <p>
 * https://leetcode-cn.com/problems/evaluate-reverse-polish-notation/
 * <p>
 * tokens = ["2","1","+","3","*"]
 * 该算式转化为常见的中缀算术表达式为：((2 + 1) * 3) = 9
 */
public class Question_150 {

    public int evalRPN(String[] tokens) {
        Set<String> operator = new HashSet<>(Arrays.asList("+", "-", "*", "/"));
        Stack<Integer> stack = new Stack<>();
        for (String token : tokens) {
            if (operator.contains(token)) {
                int top = stack.pop();
                int second = stack.pop();
                stack.push(calculate(second, top, token));
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.peek();
    }

    private int calculate(int x, int y, String token) {
        if ("+".equals(token)) {
            return x + y;
        }
        if ("-".equals(token)) {
            return x - y;
        }
        if ("*".equals(token)) {
            return x * y;
        }
        return x / y;
    }
}
