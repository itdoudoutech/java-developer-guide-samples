package com.doudou.user.web.listener;


import com.doudou.context.ComponentContext;
import com.doudou.context.WebApplicationContextListener;
import com.doudou.user.sql.DBConnectionManager;
import org.apache.commons.io.FileUtils;

import javax.annotation.Priority;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@Priority(value = 2)
public class WebApplicationInitializerListener implements WebApplicationContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initDb();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void initDb() throws RuntimeException {
        DBConnectionManager dbConnectionManager = ComponentContext.getInstance().getComponent("bean/DBConnectionManager");
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
}
