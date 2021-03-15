package com.doudou.user.jmx;

import javax.management.ObjectName;

public class UserManagementMBeanDemo {

    public static void main(String[] args) throws Exception {
        MBeanHelper.registerMBean(new UserManagement(),
                new ObjectName("com.doudou.user.jmx:type=UserManagement"));
        System.out.println("按任意键退出...");
        System.in.read();
    }
}
