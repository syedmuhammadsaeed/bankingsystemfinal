package bankingsystemfinal;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/banking";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC driver (optional for newer JDBC versions, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected to the database.");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }
}