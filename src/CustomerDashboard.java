package bankingsystemfinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard {
    private int customerId;

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public List<bankingsystemfinal.Account> getAccounts() {
        List<bankingsystemfinal.Account> accounts = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch accounts.");
            return accounts;
        }

        try {
            String sql = "SELECT * FROM accounts WHERE customer_id = ?";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                accounts.add(new bankingsystemfinal.Account(
                        rs.getInt("account_id"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                ));
            }
            System.out.println("Fetched " + accounts.size() + " account(s).");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return accounts;
    }

    public List<bankingsystemfinal.TransactionHistory.TransactionRecord> getTransactions() {
        List<bankingsystemfinal.TransactionHistory.TransactionRecord> transactions = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch transactions.");
            return transactions;
        }

        try {
            String sql = "SELECT * FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                transactions.add(new bankingsystemfinal.TransactionHistory.TransactionRecord(
                        rs.getInt("account_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp")
                ));
            }
            System.out.println("Fetched " + transactions.size() + " transaction(s).");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return transactions;
    }

    public List<bankingsystemfinal.AdminDashboard.DepositRequest> getDepositRequests() {
        List<bankingsystemfinal.AdminDashboard.DepositRequest> requests = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch deposit requests.");
            return requests;
        }

        try {
            String sql = "SELECT * FROM deposit_requests WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                requests.add(new bankingsystemfinal.AdminDashboard.DepositRequest(
                        rs.getInt("account_id"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            System.out.println("Fetched " + requests.size() + " deposit request(s).");
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

    public boolean deposit(int accountId, double amount) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "INSERT INTO deposit_requests (account_id, amount, timestamp, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, java.time.LocalDateTime.now().toString());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error requesting deposit: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public List<bankingsystemfinal.Loan.LoanRecord> getLoanRequests() {
        List<bankingsystemfinal.Loan.LoanRecord> loans = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch loan requests.");
            return loans;
        }

        try {
            String sql = "SELECT * FROM loans WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                loans.add(new bankingsystemfinal.Loan.LoanRecord(
                        rs.getInt("account_id"),
                        rs.getDouble("amount"),
                        rs.getString("loan_type"),
                        rs.getString("applied_date"),
                        rs.getString("status")
                ));
            }
            System.out.println("Fetched " + loans.size() + " loan request(s).");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching loan requests: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return loans;
    }

    public boolean requestLoan(int accountId, double amount) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "INSERT INTO loans (account_id, amount, loan_type, applied_date, status) VALUES (?, ?, 'Personal', ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, java.time.LocalDateTime.now().toString());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error requesting loan: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public List<bankingsystemfinal.Transfer> getTransfers() {
        List<bankingsystemfinal.Transfer> transfers = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch transfers.");
            return transfers;
        }

        try {
            String sql = "SELECT * FROM transfers WHERE account_id_from IN (SELECT account_id FROM accounts WHERE customer_id = ?) OR account_id_to IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                transfers.add(new bankingsystemfinal.Transfer(
                        rs.getInt("account_id_from"),
                        rs.getInt("account_id_to"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            System.out.println("Fetched " + transfers.size() + " transfer(s).");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching transfers: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return transfers;
    }

    public boolean transfer(int accountIdFrom, int accountIdTo, double amount) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "INSERT INTO transfers (account_id_from, account_id_to, amount, timestamp, status) VALUES (?, ?, ?, ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountIdFrom);
            stmt.setInt(2, accountIdTo);
            stmt.setDouble(3, amount);
            stmt.setString(4, java.time.LocalDateTime.now().toString());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error requesting transfer: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}