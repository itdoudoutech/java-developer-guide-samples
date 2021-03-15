package com.doudou.user.web.listener;


import com.doudou.context.ComponentContext;
import com.doudou.user.domain.User;
import com.doudou.user.jmx.MBeanHelper;
import com.doudou.user.jmx.UserManagement;
import com.doudou.user.sql.DBConnectionManager;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        // 读取配置
        testMicroprofileConfig();

        // 注册 MBean
        registerMBean();
    }

    private void registerMBean() {
        try {
            MBeanHelper.registerMBean(new UserManagement(),
                    new ObjectName("com.doudou.user.jmx:type=UserManagement"));
            logger.info("registerMBean success...");
        }catch (Exception e){
            logger.info("registerMBean throw exception, msg = " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void testMicroprofileConfig() {
        logger.info("read config by microprofile.config:");
        ConfigProviderResolver provider = ConfigProviderResolver.instance();

        Config config = provider.getConfig();

        String applicationName = config.getValue("application.name", String.class);

        Byte byteValue = config.getValue("id", Byte.class);
        Short shortValue = config.getValue("id", Short.class);
        Character charValue = config.getValue("id", Character.class);

        Integer integerValue = config.getValue("id", Integer.class);
        Long longValue = config.getValue("id", Long.class);
        Boolean debug = config.getValue("debug", Boolean.class);

        Float floatValue = config.getValue("version", Float.class);
        Double doubleValue = config.getValue("version", Double.class);

        logger.info(String.format("converter string value [application.name = %s]", applicationName));
        logger.info(String.format("converter byte value [id = %s]", byteValue));
        logger.info(String.format("converter short value [id = %s]", shortValue));
        logger.info(String.format("converter char value [id = %s]", charValue));
        logger.info(String.format("converter int value [id = %s]", integerValue));
        logger.info(String.format("converter long value [id = %s]", longValue));
        logger.info(String.format("converter boolean value [debug switch = %s]", debug));
        logger.info(String.format("converter float value [version = %s]", floatValue));
        logger.info(String.format("converter double value [version = %s]", doubleValue));

    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        user.setId(null);
        user.setName("小马哥");
        user.setPassword("******");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("13466668888");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        logger.info("testInsertUser userId = " + user.getId());
        System.out.println(entityManager.find(User.class, user.getId()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
