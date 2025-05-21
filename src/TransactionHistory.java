package bankingsystemfinal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistory {
    public static class TransactionRecord {
        private int accountId;
        private String type;
        private double amount;
        private String timestamp;

        public TransactionRecord(int accountId, String type, double amount, String timestamp) {
            this.accountId = accountId;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public int getAccountId() {
            return accountId;
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public List<TransactionRecord> getTransactions() {
        List<TransactionRecord> transactions = new ArrayList<>();
        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.out.println("Connection is null, cannot fetch transactions.");
            return transactions;
        }

        try {
            String sql = "SELECT * FROM transactions";
            System.out.println("Executing query: " + sql);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            System.out.println("ResultSet retrieved, processing rows...");
            while (rs.next()) {
                transactions.add(new TransactionRecord(
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
}