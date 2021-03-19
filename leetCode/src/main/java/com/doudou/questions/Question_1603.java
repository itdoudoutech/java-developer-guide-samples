package com.doudou.questions;


/**
 * 请你给一个停车场设计一个停车系统。停车场总共有三种不同大小的车位：大，中和小，每种尺寸分别有固定数目的车位。
 * carType 有三种类型：大，中，小，分别用数字 1， 2 和 3 表示
 * <p>
 * https://leetcode-cn.com/problems/design-parking-system/
 */
public class Question_1603 {

    class ParkingSystem {
        int[] count = new int[4];

        public ParkingSystem(int big, int medium, int small) {
            count[1] = big;
            count[2] = medium;
            count[3] = small;
        }

        public boolean addCar(int carType) {
            count[carType] = count[carType] - 1;
            return count[carType] < 0;
        }
    }
}
