package com.doudou.user.jmx;

import java.util.logging.Logger;

public class UserManagement implements UserManagementMBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
        logger.info("UserManagement setName method invoke: " + name);
    }

    @Override
    public String getName() {
        logger.info("UserManagement getName method invoke: " + name);
        return name;
    }
}
