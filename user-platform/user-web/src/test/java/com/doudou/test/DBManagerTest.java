package com.doudou.test;

import java.sql.*;

public class DBManagerTest {

    private final static String URL = "jdbc:h2:mem:~/user-platform;DB_CLOSE_DELAY=-1";
    private final static String USERNAME = "sa";
    private final static String PASSWORD = "sa";
    private final static String DRIVER_CLASS_NAME = "org.h2.Driver";

    private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS users";
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "id int(20) NOT NULL COMMENT 'primary key id'," +
            "name varchar(64) NOT NULL COMMENT 'user name'," +
            "password varchar(128) DEFAULT NULL COMMENT 'user password'," +
            "email varchar(128) DEFAULT NULL COMMENT 'user email'," +
            "phoneNumber varchar(128) DEFAULT NULL COMMENT 'user phoneNumber'," +
            "sex int(1) NOT NULL COMMENT 'sex'," +
            "PRIMARY KEY (id)" +
            ");";
    private static final String INSERT_USERS = "INSERT INTO users VALUES (1, 'admin', 'admin', '123@qq.com', '13466775544', 1)";

    private static Connection connection;

    static {
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate(DROP_USERS_TABLE);
            statement.executeUpdate(CREATE_USERS_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName(DRIVER_CLASS_NAME);
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        final String sql = "SELECT id, name, password FROM users WHERE id = ?";
        final int id = 1;
        Statement statement = connection.createStatement();
        int dropTable = statement.executeUpdate(DROP_USERS_TABLE);
        int createTable = statement.executeUpdate(CREATE_USERS_TABLE);
        int insertUsers = statement.executeUpdate(INSERT_USERS);
        System.out.printf("dropTable = %d, createTable = %d, insertUsers = %d%n", dropTable, createTable, insertUsers);

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            String dbId = resultSet.getString("id");
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");
            System.out.printf("id = %s, username = %s, password = %s%n", dbId, name, password);
        }
    }

}
