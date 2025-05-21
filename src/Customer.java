package bankingsystemfinal;

import java.sql.*;

public class Customer {
    public static boolean validateCustomerLogin(String email, String password) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to validate login: Database connection is null.");
            return false;
        }

        try {
            String sql = "SELECT * FROM customer WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            boolean valid = rs.next();
            stmt.close();
            return valid;
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static int getCustomerId(String email) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to get customer ID: Database connection is null.");
            return -1;
        }

        try {
            String sql = "SELECT customer_id FROM customer WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error getting customer ID: " + e.getMessage());
            return -1;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean registerCustomer(String name, String email, String password, String phoneNumber) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to register customer: Database connection is null.");
            return false;
        }

        try {
            String sql = "INSERT INTO customer (name, email, password, phone_number) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phoneNumber);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering customer: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static class CustomerRecord {
        private int customerId;
        private String name;
        private String email;
        private String phoneNumber;

        public CustomerRecord(int customerId, String name, String email, String phoneNumber) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public int getCustomerId() { return customerId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
    }
}