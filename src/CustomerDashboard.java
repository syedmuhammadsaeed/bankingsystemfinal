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

    public List<TransactionRecord> getTransactions() {
        List<TransactionRecord> transactions = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch transactions.");
            return transactions;
        }

        try {
            // Fetch completed transactions
            String sqlTransactions = "SELECT * FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sqlTransactions + " with customerId = " + customerId);
            PreparedStatement stmtTransactions = conn.prepareStatement(sqlTransactions);
            stmtTransactions.setInt(1, customerId);
            ResultSet rsTransactions = stmtTransactions.executeQuery();
            while (rsTransactions.next()) {
                transactions.add(new TransactionRecord(
                        rsTransactions.getInt("account_id"),
                        0, // No "Account ID To" for regular transactions
                        rsTransactions.getString("type"),
                        rsTransactions.getDouble("amount"),
                        rsTransactions.getString("timestamp"),
                        "Completed"
                ));
            }
            stmtTransactions.close();

            // Fetch deposit requests
            String sqlDeposits = "SELECT * FROM deposit_requests WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sqlDeposits + " with customerId = " + customerId);
            PreparedStatement stmtDeposits = conn.prepareStatement(sqlDeposits);
            stmtDeposits.setInt(1, customerId);
            ResultSet rsDeposits = stmtDeposits.executeQuery();
            while (rsDeposits.next()) {
                transactions.add(new TransactionRecord(
                        rsDeposits.getInt("account_id"),
                        0, // No "Account ID To" for deposits
                        "Deposit Request",
                        rsDeposits.getDouble("amount"),
                        rsDeposits.getString("timestamp"),
                        rsDeposits.getString("status")
                ));
            }
            stmtDeposits.close();

            // Fetch withdraw requests
            String sqlWithdraws = "SELECT * FROM withdraw_requests WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sqlWithdraws + " with customerId = " + customerId);
            PreparedStatement stmtWithdraws = conn.prepareStatement(sqlWithdraws);
            stmtWithdraws.setInt(1, customerId);
            ResultSet rsWithdraws = stmtWithdraws.executeQuery();
            while (rsWithdraws.next()) {
                transactions.add(new TransactionRecord(
                        rsWithdraws.getInt("account_id"),
                        0, // No "Account ID To" for withdraws
                        "Withdraw Request",
                        rsWithdraws.getDouble("amount"),
                        rsWithdraws.getString("timestamp"),
                        rsWithdraws.getString("status")
                ));
            }
            stmtWithdraws.close();

            // Fetch transfers
            String sqlTransfers = "SELECT * FROM transfers WHERE account_id_from IN (SELECT account_id FROM accounts WHERE customer_id = ?) OR account_id_to IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sqlTransfers + " with customerId = " + customerId);
            PreparedStatement stmtTransfers = conn.prepareStatement(sqlTransfers);
            stmtTransfers.setInt(1, customerId);
            stmtTransfers.setInt(2, customerId);
            ResultSet rsTransfers = stmtTransfers.executeQuery();
            while (rsTransfers.next()) {
                transactions.add(new TransactionRecord(
                        rsTransfers.getInt("account_id_from"),
                        rsTransfers.getInt("account_id_to"),
                        "Transfer Request",
                        rsTransfers.getDouble("amount"),
                        rsTransfers.getString("timestamp"),
                        rsTransfers.getString("status")
                ));
            }
            stmtTransfers.close();

            System.out.println("Fetched " + transactions.size() + " transaction(s) including requests.");
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

    // Inner class to represent a unified transaction record
    public static class TransactionRecord {
        private int accountIdFrom;
        private int accountIdTo; // 0 if not applicable (e.g., for deposits, withdraws, regular transactions)
        private String type;
        private double amount;
        private String timestamp;
        private String status;

        public TransactionRecord(int accountIdFrom, int accountIdTo, String type, double amount, String timestamp, String status) {
            this.accountIdFrom = accountIdFrom;
            this.accountIdTo = accountIdTo;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
            this.status = status;
        }

        public int getAccountIdFrom() { return accountIdFrom; }
        public int getAccountIdTo() { return accountIdTo; }
        public String getType() { return type; }
        public double getAmount() { return amount; }
        public String getTimestamp() { return timestamp; }
        public String getStatus() { return status; }
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

    public static class Notification {
        private int id;
        private String message;
        private String timestamp;

        public Notification(int id, String message, String timestamp) {
            this.id = id;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getId() { return id; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }

    public List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch notifications.");
            return notifications;
        }

        try {
            String sql = "SELECT * FROM notifications WHERE customer_id = ? ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getString("timestamp")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return notifications;
    }

    public List<bankingsystemfinal.AdminDashboard.WithdrawRequest> getWithdrawRequests() {
        List<bankingsystemfinal.AdminDashboard.WithdrawRequest> requests = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch withdraw requests.");
            return requests;
        }

        try {
            String sql = "SELECT * FROM withdraw_requests WHERE account_id IN (SELECT account_id FROM accounts WHERE customer_id = ?)";
            System.out.println("Executing query: " + sql + " with customerId = " + customerId);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                requests.add(new bankingsystemfinal.AdminDashboard.WithdrawRequest(
                        rs.getInt("account_id"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            System.out.println("Fetched " + requests.size() + " withdraw request(s).");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching withdraw requests: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return requests;
    }

    public boolean withdraw(int accountId, double amount) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "INSERT INTO withdraw_requests (account_id, amount, timestamp, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, java.time.LocalDateTime.now().toString());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error requesting withdraw: " + e.getMessage());
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