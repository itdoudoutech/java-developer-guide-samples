package com.doudou.questions;


import com.doudou.base.ListNode;

/**
 * 给你一个单链表的引用结点 head。链表中每个结点的值不是 0 就是 1。已知此链表是一个整数数字的二进制表示形式。
 * 请你返回该链表所表示数字的 十进制值 。
 * <p>
 * https://leetcode-cn.com/problems/convert-binary-number-in-a-linked-list-to-integer
 */
public class Question_1290 {
    public int getDecimalValue(ListNode head) {
        int x = 0;
        while (null != head) {
            x = x | head.val;
            head = head.next;
            x = x << 1;
        }
        return x;
    }
}
