package com.doudou.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class WebApplicationContextListenerInitializer implements ServletContextListener {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    private final static List<WebApplicationContextListener> contextListeners = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServiceLoader<WebApplicationContextListener> webApplicationContextListeners
                = ServiceLoader.load(WebApplicationContextListener.class);
        webApplicationContextListeners.forEach(contextListeners::add);
        contextListeners.sort(WebApplicationContextListenerOrdinalComparator.INSTANCE);

        // 分发 WebApplicationContextListener
        for (WebApplicationContextListener contextListener : contextListeners) {
            contextListener.contextInitialized(sce);
            logger.info(contextListener.getClass().getSimpleName() + " Listener contextInitialized success");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (int i = contextListeners.size() - 1; i >= 0; i--) {
            contextListeners.get(i).contextDestroyed(sce);
        }
    }
}
