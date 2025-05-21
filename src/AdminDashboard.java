package bankingsystemfinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard {
    public static class DepositRequest {
        private int accountId;
        private double amount;
        private String timestamp;
        private String status;

        public DepositRequest(int accountId, double amount, String timestamp, String status) {
            this.accountId = accountId;
            this.amount = amount;
            this.timestamp = timestamp;
            this.status = status;
        }

        public int getAccountId() { return accountId; }
        public double getAmount() { return amount; }
        public String getTimestamp() { return timestamp; }
        public String getStatus() { return status; }
    }

    public static class WithdrawRequest {
        private int accountId;
        private double amount;
        private String timestamp;
        private String status;

        public WithdrawRequest(int accountId, double amount, String timestamp, String status) {
            this.accountId = accountId;
            this.amount = amount;
            this.timestamp = timestamp;
            this.status = status;
        }

        public int getAccountId() { return accountId; }
        public double getAmount() { return amount; }
        public String getTimestamp() { return timestamp; }
        public String getStatus() { return status; }
    }

    public static class DashboardMetrics {
        private int clientCount;
        private int activeLoans;
        private double monthlyRevenue;

        public DashboardMetrics(int clientCount, int activeLoans, double monthlyRevenue) {
            this.clientCount = clientCount;
            this.activeLoans = activeLoans;
            this.monthlyRevenue = monthlyRevenue;
        }

        public int getClientCount() { return clientCount; }
        public int getActiveLoans() { return activeLoans; }
        public double getMonthlyRevenue() { return monthlyRevenue; }
    }

    public List<bankingsystemfinal.Customer.CustomerRecord> getCustomerDetails() {
        List<bankingsystemfinal.Customer.CustomerRecord> customers = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return customers;

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
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
        return customers;
    }

    public List<bankingsystemfinal.TransactionHistory.TransactionRecord> getTransactionHistory() {
        bankingsystemfinal.TransactionHistory history = new bankingsystemfinal.TransactionHistory();
        return history.getTransactions();
    }

    public List<DepositRequest> getDepositRequests() {
        List<DepositRequest> requests = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return requests;

        try {
            String sql = "SELECT * FROM deposit_requests";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(new DepositRequest(
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
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
        return requests;
    }

    public List<WithdrawRequest> getWithdrawRequests() {
        List<WithdrawRequest> requests = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return requests;

        try {
            String sql = "SELECT * FROM withdraw_requests";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(new WithdrawRequest(
                        rs.getInt("account_id"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching withdraw requests: " + e.getMessage());
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
        return requests;
    }

    public List<bankingsystemfinal.Loan.LoanRecord> getLoanRequests() {
        bankingsystemfinal.Loan loan = new bankingsystemfinal.Loan();
        return loan.getLoans();
    }

    public List<bankingsystemfinal.Transfer> getTransferRequests() {
        List<bankingsystemfinal.Transfer> transfers = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return transfers;

        try {
            String sql = "SELECT * FROM transfers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transfers.add(new bankingsystemfinal.Transfer(
                        rs.getInt("account_id_from"),
                        rs.getInt("account_id_to"),
                        rs.getDouble("amount"),
                        rs.getString("timestamp"),
                        rs.getString("status")
                ));
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fetching transfer requests: " + e.getMessage());
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
        return transfers;
    }

    public boolean approveDeposit(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String updateRequestSql = "UPDATE deposit_requests SET status = ?, admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement updateStmt = conn.prepareStatement(updateRequestSql);
            updateStmt.setString(1, "Approved");
            updateStmt.setInt(2, adminId);
            updateStmt.setInt(3, accountId);
            updateStmt.setDouble(4, amount);
            int requestRowsAffected = updateStmt.executeUpdate();
            updateStmt.close();

            if (requestRowsAffected == 0) {
                conn.rollback();
                System.err.println("No pending deposit request found for account_id: " + accountId + " and amount: " + amount);
                return false;
            }

            String updateBalanceSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceSql);
            balanceStmt.setDouble(1, amount);
            balanceStmt.setInt(2, accountId);
            int balanceRowsAffected = balanceStmt.executeUpdate();
            balanceStmt.close();

            if (balanceRowsAffected == 0) {
                conn.rollback();
                System.err.println("Failed to update balance: Account not found for account_id: " + accountId);
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error approving deposit: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean disapproveDeposit(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "UPDATE deposit_requests SET status = 'Disapproved', admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setInt(2, accountId);
            stmt.setDouble(3, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error disapproving deposit: " + e.getMessage());
            return false;
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }

    public boolean approveWithdraw(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            // Check account balance
            String checkBalanceSql = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSql);
            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next() || rs.getDouble("balance") < amount) {
                conn.rollback();
                System.err.println("Insufficient balance or account not found for account_id: " + accountId);
                checkStmt.close();
                return false;
            }
            checkStmt.close();

            // Update withdraw request status
            String updateRequestSql = "UPDATE withdraw_requests SET status = ?, admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement updateStmt = conn.prepareStatement(updateRequestSql);
            updateStmt.setString(1, "Approved");
            updateStmt.setInt(2, adminId);
            updateStmt.setInt(3, accountId);
            updateStmt.setDouble(4, amount);
            int requestRowsAffected = updateStmt.executeUpdate();
            updateStmt.close();

            if (requestRowsAffected == 0) {
                conn.rollback();
                System.err.println("No pending withdraw request found for account_id: " + accountId + " and amount: " + amount);
                return false;
            }

            // Update account balance
            String updateBalanceSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceSql);
            balanceStmt.setDouble(1, amount);
            balanceStmt.setInt(2, accountId);
            int balanceRowsAffected = balanceStmt.executeUpdate();
            balanceStmt.close();

            if (balanceRowsAffected == 0) {
                conn.rollback();
                System.err.println("Failed to update balance: Account not found for account_id: " + accountId);
                return false;
            }

            // Record transaction
            String insertTransactionSql = "INSERT INTO transactions (account_id, type, amount, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement transactionStmt = conn.prepareStatement(insertTransactionSql);
            transactionStmt.setInt(1, accountId);
            transactionStmt.setString(2, "Withdraw");
            transactionStmt.setDouble(3, amount);
            transactionStmt.setString(4, java.time.LocalDateTime.now().toString());
            transactionStmt.executeUpdate();
            transactionStmt.close();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error approving withdraw: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean rejectWithdraw(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "UPDATE withdraw_requests SET status = 'Rejected', admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setInt(2, accountId);
            stmt.setDouble(3, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting withdraw: " + e.getMessage());
            return false;
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }

    public boolean approveLoan(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String updateLoanSql = "UPDATE loans SET status = ?, admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement loanStmt = conn.prepareStatement(updateLoanSql);
            loanStmt.setString(1, "Approved");
            loanStmt.setInt(2, adminId);
            loanStmt.setInt(3, accountId);
            loanStmt.setDouble(4, amount);
            int loanRowsAffected = loanStmt.executeUpdate();
            loanStmt.close();

            if (loanRowsAffected == 0) {
                conn.rollback();
                System.err.println("No pending loan request found for account_id: " + accountId + " and amount: " + amount);
                return false;
            }

            String updateBalanceSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(updateBalanceSql);
            balanceStmt.setDouble(1, amount);
            balanceStmt.setInt(2, accountId);
            int balanceRowsAffected = balanceStmt.executeUpdate();
            balanceStmt.close();

            if (balanceRowsAffected == 0) {
                conn.rollback();
                System.err.println("Failed to update balance: Account not found for account_id: " + accountId);
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error approving loan: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean rejectLoan(int accountId, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "UPDATE loans SET status = 'Rejected', admin_id = ? WHERE account_id = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setInt(2, accountId);
            stmt.setDouble(3, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting loan: " + e.getMessage());
            return false;
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }

    public boolean approveTransfer(int accountIdFrom, int accountIdTo, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String updateTransferSql = "UPDATE transfers SET status = ?, admin_id = ? WHERE account_id_from = ? AND account_id_to = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement transferStmt = conn.prepareStatement(updateTransferSql);
            transferStmt.setString(1, "Approved");
            transferStmt.setInt(2, adminId);
            transferStmt.setInt(3, accountIdFrom);
            transferStmt.setInt(4, accountIdTo);
            transferStmt.setDouble(5, amount);
            int transferRowsAffected = transferStmt.executeUpdate();
            transferStmt.close();

            if (transferRowsAffected == 0) {
                conn.rollback();
                System.err.println("No pending transfer request found for account_id_from: " + accountIdFrom + ", account_id_to: " + accountIdTo + ", amount: " + amount);
                return false;
            }

            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement debitStmt = conn.prepareStatement(debitSql);
            debitStmt.setDouble(1, amount);
            debitStmt.setInt(2, accountIdFrom);
            int debitRowsAffected = debitStmt.executeUpdate();
            debitStmt.close();

            if (debitRowsAffected == 0) {
                conn.rollback();
                System.err.println("Failed to debit account: Account not found for account_id: " + accountIdFrom);
                return false;
            }

            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement creditStmt = conn.prepareStatement(creditSql);
            creditStmt.setDouble(1, amount);
            creditStmt.setInt(2, accountIdTo);
            int creditRowsAffected = creditStmt.executeUpdate();
            creditStmt.close();

            if (creditRowsAffected == 0) {
                conn.rollback();
                System.err.println("Failed to credit account: Account not found for account_id: " + accountIdTo);
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error approving transfer: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public boolean rejectTransfer(int accountIdFrom, int accountIdTo, double amount, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "UPDATE transfers SET status = 'Rejected', admin_id = ? WHERE account_id_from = ? AND account_id_to = ? AND amount = ? AND status = 'Pending'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setInt(2, accountIdFrom);
            stmt.setInt(3, accountIdTo);
            stmt.setDouble(4, amount);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting transfer: " + e.getMessage());
            return false;
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }

    public boolean sendNotification(int customerId, String message) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            String sql = "INSERT INTO notifications (customer_id, message, timestamp) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setString(2, message);
            stmt.setString(3, java.time.LocalDateTime.now().toString());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error sending notification: " + e.getMessage());
            return false;
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }

    public DashboardMetrics getDashboardMetrics() {
        return new DashboardMetrics(0, 0, 0.0);
    }
}