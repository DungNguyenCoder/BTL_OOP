package com.btl_oop.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    public static Connection getConnection() throws SQLException
    {
        String URL ="jdbc:mysql://127.0.0.1:3306/restaurant_management";
        String USER = "root";
        String PASSWORD ="123456";
        return DriverManager.getConnection(URL , USER, PASSWORD);

    }

}
