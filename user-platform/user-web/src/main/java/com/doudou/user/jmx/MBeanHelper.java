package com.doudou.user.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * MBeanHelper
 */
public class MBeanHelper {

    private static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    public static void registerMBean(Object object, ObjectName name) throws
            NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        mBeanServer.registerMBean(object, name);
    }
}
