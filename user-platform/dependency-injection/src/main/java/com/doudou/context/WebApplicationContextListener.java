package com.doudou.context;

import javax.servlet.ServletContextEvent;

/**
 * 替代 ServletContextListener
 */
public interface WebApplicationContextListener {

    /**
     * init
     *
     * @param sce ServletContextEvent
     */
    void contextInitialized(ServletContextEvent sce);

    /**
     * destroy
     *
     * @param sce ServletContextEvent
     */
    void contextDestroyed(ServletContextEvent sce);
}
