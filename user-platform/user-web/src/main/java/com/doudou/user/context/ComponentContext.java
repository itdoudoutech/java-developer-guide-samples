package com.doudou.user.context;

import com.doudou.user.sql.DBConnectionManager;
import org.apache.commons.io.FileUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ComponentContext {

    private static final Logger logger = Logger.getLogger(ComponentContext.class.getName());


    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    private static ServletContext servletContext; // 请注意
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

//    private static ApplicationContext applicationContext;

//    public void setApplicationContext(ApplicationContext applicationContext){
//        ComponentContext.applicationContext = applicationContext;
//        WebApplicationContextUtils.getRootWebApplicationContext()
//    }

    /**
     * 获取 ComponentContext
     *
     * @return
     */
    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    private Context context;

    public void init(ServletContext servletContext) throws RuntimeException {
        try {
            this.context = (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        servletContext.setAttribute(CONTEXT_NAME, this);
        ComponentContext.servletContext = servletContext;
        logger.info("ComponentContext init success");

        // init tables
        initDb();
    }

    private void initDb() throws RuntimeException {
        DBConnectionManager dbConnectionManager = getComponent("bean/DBConnectionManager");
        Connection connection = dbConnectionManager.getConnection();
        try {
            //获取文件的URL
            URL url = this.getClass().getClassLoader().getResource("META-INF/schema.h2.sql");
            String script = FileUtils.readFileToString(new File(url.getPath()), StandardCharsets.UTF_8);
            Statement statement = connection.createStatement();
            statement.executeUpdate(script);

            // logger.info("schema_h2 script: " + script);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 通过名称进行依赖查找
     *
     * @param name
     * @param <C>
     * @return
     */
    public <C> C getComponent(String name) {
        C component = null;
        try {
            component = (C) context.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchElementException(name);
        }
        return component;
    }

    public void destroy() throws RuntimeException {
        if (this.context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
