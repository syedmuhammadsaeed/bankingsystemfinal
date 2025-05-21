package bankingsystemfinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin {
    public boolean validateAdminLogin(String email, String password) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to validate admin login: Database connection is null.");
            return false;
        }

        try {
            String sql = "SELECT * FROM admin WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            boolean valid = rs.next();
            stmt.close();
            return valid;
        } catch (SQLException e) {
            System.err.println("Error validating admin login: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public int getAdminId(String email) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to get admin ID: Database connection is null.");
            return -1;
        }

        try {
            String sql = "SELECT admin_id FROM admin WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("admin_id");
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error getting admin ID: " + e.getMessage());
            return -1;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public List<bankingsystemfinal.Customer.CustomerRecord> getAllCustomers() {
        List<bankingsystemfinal.Customer.CustomerRecord> customers = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to fetch customers: Database connection is null.");
            return customers;
        }

        try {
            String sql = "SELECT * FROM customer";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(new bankingsystemfinal.Customer.CustomerRecord(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return customers;
    }

    public List<bankingsystemfinal.TransactionHistory.TransactionRecord> getTransactionHistory() {
        bankingsystemfinal.TransactionHistory history = new bankingsystemfinal.TransactionHistory();
        return history.getTransactions(); // Placeholder implementation
    }

    public List<bankingsystemfinal.AdminDashboard.DepositRequest> getDepositRequests() {
        List<bankingsystemfinal.AdminDashboard.DepositRequest> requests = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to fetch deposit requests: Database connection is null.");
            return requests;
        }

        try {
            String sql = "SELECT * FROM deposit_requests";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(new bankingsystemfinal.AdminDashboard.DepositRequest(
                        rs.getInt("account_id"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching deposit requests: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return requests;
    }

    public List<bankingsystemfinal.Loan.LoanRecord> getLoanRequests() {
        bankingsystemfinal.Loan loan = new bankingsystemfinal.Loan();
        return loan.getLoans(); // Placeholder implementation
    }

    public boolean approveDepositRequest(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to approve deposit request: Database connection is null.");
            return false;
        }

        try {
            String sql = "UPDATE deposit_requests SET status = 'Approved' WHERE account_id = ? AND amount = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error approving deposit request: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean disapproveDepositRequest(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to disapprove deposit request: Database connection is null.");
            return false;
        }

        try {
            String sql = "UPDATE deposit_requests SET status = 'Disapproved' WHERE account_id = ? AND amount = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error disapproving deposit request: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean approveLoanRequest(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to approve loan request: Database connection is null.");
            return false;
        }

        try {
            String sql = "UPDATE loans SET status = 'Approved' WHERE account_id = ? AND amount = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error approving loan request: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean rejectLoanRequest(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to reject loan request: Database connection is null.");
            return false;
        }

        try {
            String sql = "UPDATE loans SET status = 'Rejected' WHERE account_id = ? AND amount = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting loan request: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static class AdminRecord {
        private int adminId;
        private String email;

        public AdminRecord(int adminId, String email) {
            this.adminId = adminId;
            this.email = email;
        }

        public int getAdminId() {
            return adminId;
        }

        public String getEmail() {
            return email;
        }
    }
}