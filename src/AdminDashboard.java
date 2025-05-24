package bankingsystemfinal;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static class TransactionRecord {
        private int accountIdFrom;
        private int accountIdTo;
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

    public static class DashboardMetrics {
        private int clientCount;
        private int activeLoans;
        private double monthlyRevenue;
        private int monthlyDeposits;
        private int monthlyTransfers;
        private int monthlyWithdrawals;
        private double totalIn;
        private double totalOut;

        public DashboardMetrics(int clientCount, int activeLoans, double monthlyRevenue, int monthlyDeposits, int monthlyTransfers, int monthlyWithdrawals, double totalIn, double totalOut) {
            this.clientCount = clientCount;
            this.activeLoans = activeLoans;
            this.monthlyRevenue = monthlyRevenue;
            this.monthlyDeposits = monthlyDeposits;
            this.monthlyTransfers = monthlyTransfers;
            this.monthlyWithdrawals = monthlyWithdrawals;
            this.totalIn = totalIn;
            this.totalOut = totalOut;
        }

        public int getClientCount() { return clientCount; }
        public int getActiveLoans() { return activeLoans; }
        public double getMonthlyRevenue() { return monthlyRevenue; }
        public int getMonthlyDeposits() { return monthlyDeposits; }
        public int getMonthlyTransfers() { return monthlyTransfers; }
        public int getMonthlyWithdrawals() { return monthlyWithdrawals; }
        public double getTotalIn() { return totalIn; }
        public double getTotalOut() { return totalOut; }
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

    public List<TransactionRecord> getTransactionHistory() {
        List<TransactionRecord> transactions = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return transactions;

        try {
            String sqlTransactions = "SELECT * FROM transactions";
            PreparedStatement stmtTransactions = conn.prepareStatement(sqlTransactions);
            ResultSet rsTransactions = stmtTransactions.executeQuery();
            while (rsTransactions.next()) {
                transactions.add(new TransactionRecord(
                        rsTransactions.getInt("account_id"),
                        0,
                        rsTransactions.getString("type"),
                        rsTransactions.getDouble("amount"),
                        rsTransactions.getString("timestamp"),
                        "Completed"
                ));
            }
            stmtTransactions.close();

            String sqlDeposits = "SELECT * FROM deposit_requests";
            PreparedStatement stmtDeposits = conn.prepareStatement(sqlDeposits);
            ResultSet rsDeposits = stmtDeposits.executeQuery();
            while (rsDeposits.next()) {
                transactions.add(new TransactionRecord(
                        rsDeposits.getInt("account_id"),
                        0,
                        "Deposit Request",
                        rsDeposits.getDouble("amount"),
                        rsDeposits.getString("timestamp"),
                        rsDeposits.getString("status")
                ));
            }
            stmtDeposits.close();

            String sqlWithdraws = "SELECT * FROM withdraw_requests";
            PreparedStatement stmtWithdraws = conn.prepareStatement(sqlWithdraws);
            ResultSet rsWithdraws = stmtWithdraws.executeQuery();
            while (rsWithdraws.next()) {
                transactions.add(new TransactionRecord(
                        rsWithdraws.getInt("account_id"),
                        0,
                        "Withdraw Request",
                        rsWithdraws.getDouble("amount"),
                        rsWithdraws.getString("timestamp"),
                        rsWithdraws.getString("status")
                ));
            }
            stmtWithdraws.close();

            String sqlTransfers = "SELECT * FROM transfers";
            PreparedStatement stmtTransfers = conn.prepareStatement(sqlTransfers);
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
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
        return transactions;
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
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return new DashboardMetrics(0, 0, 0.0, 0, 0, 0, 0.0, 0.0);

        int clientCount = 0;
        int activeLoans = 0;
        int monthlyDeposits = 0;
        int monthlyTransfers = 0;
        int monthlyWithdrawals = 0;
        double totalIn = 0.0;
        double totalOut = 0.0;
        double monthlyRevenue = 0.0;

        LocalDateTime now = LocalDateTime.now();
        String monthStart = now.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        String monthEnd = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";

        try {
            // Client count
            String clientSql = "SELECT COUNT(*) FROM customer";
            PreparedStatement clientStmt = conn.prepareStatement(clientSql);
            ResultSet clientRs = clientStmt.executeQuery();
            if (clientRs.next()) {
                clientCount = clientRs.getInt(1);
            }
            clientStmt.close();

            // Active loans
            String loanSql = "SELECT COUNT(*) FROM loans WHERE status = 'Approved'";
            PreparedStatement loanStmt = conn.prepareStatement(loanSql);
            ResultSet loanRs = loanStmt.executeQuery();
            if (loanRs.next()) {
                activeLoans = loanRs.getInt(1);
            }
            loanStmt.close();

            // Monthly deposits
            String depositSql = "SELECT COUNT(*), SUM(amount) FROM deposit_requests WHERE status = 'Approved' AND timestamp BETWEEN ? AND ?";
            PreparedStatement depositStmt = conn.prepareStatement(depositSql);
            depositStmt.setString(1, monthStart);
            depositStmt.setString(2, monthEnd);
            ResultSet depositRs = depositStmt.executeQuery();
            if (depositRs.next()) {
                monthlyDeposits = depositRs.getInt(1);
                totalIn += depositRs.getDouble(2) != 0 ? depositRs.getDouble(2) : 0.0;
            }
            depositStmt.close();

            // Monthly withdrawals
            String withdrawSql = "SELECT COUNT(*), SUM(amount) FROM withdraw_requests WHERE status = 'Approved' AND timestamp BETWEEN ? AND ?";
            PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
            withdrawStmt.setString(1, monthStart);
            withdrawStmt.setString(2, monthEnd);
            ResultSet withdrawRs = withdrawStmt.executeQuery();
            if (withdrawRs.next()) {
                monthlyWithdrawals = withdrawRs.getInt(1);
                totalOut += withdrawRs.getDouble(2) != 0 ? withdrawRs.getDouble(2) : 0.0;
            }
            withdrawStmt.close();

            // Monthly transfers (count each transfer as one event, sum amounts for in/out)
            String transferSql = "SELECT COUNT(*), SUM(amount) FROM transfers WHERE status = 'Approved' AND timestamp BETWEEN ? AND ?";
            PreparedStatement transferStmt = conn.prepareStatement(transferSql);
            transferStmt.setString(1, monthStart);
            transferStmt.setString(2, monthEnd);
            ResultSet transferRs = transferStmt.executeQuery();
            if (transferRs.next()) {
                monthlyTransfers = transferRs.getInt(1);
                double transferAmount = transferRs.getDouble(2) != 0 ? transferRs.getDouble(2) : 0.0;
                totalIn += transferAmount; // Money coming in to account_id_to
                totalOut += transferAmount; // Money going out from account_id_from
            }
            transferStmt.close();

        } catch (SQLException e) {
            System.err.println("Error fetching dashboard metrics: " + e.getMessage());
        } finally {
            try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }

        return new DashboardMetrics(clientCount, activeLoans, monthlyRevenue, monthlyDeposits, monthlyTransfers, monthlyWithdrawals, totalIn, totalOut);
    }

    public boolean deleteCustomerAccount(int customerId, int adminId) {
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            String deleteAccountSql = "DELETE FROM accounts WHERE customer_id = ?";
            PreparedStatement accountStmt = conn.prepareStatement(deleteAccountSql);
            accountStmt.setInt(1, customerId);
            int accountRowsAffected = accountStmt.executeUpdate();
            accountStmt.close();

            if (accountRowsAffected == 0) {
                conn.rollback();
                System.err.println("No accounts found for customer_id: " + customerId);
                return false;
            }

            String deleteCustomerSql = "DELETE FROM customer WHERE customer_id = ?";
            PreparedStatement customerStmt = conn.prepareStatement(deleteCustomerSql);
            customerStmt.setInt(1, customerId);
            int customerRowsAffected = customerStmt.executeUpdate();
            customerStmt.close();

            if (customerRowsAffected == 0) {
                conn.rollback();
                System.err.println("No customer found for customer_id: " + customerId);
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
            System.err.println("Error deleting customer account: " + e.getMessage());
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
}