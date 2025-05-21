package bankingsystemfinal;

import java.util.ArrayList;
import java.util.List;

public class Loan {
    public static class LoanRecord {
        private int accountId;
        private double amount;
        private String loanType;
        private String appliedDate;
        private String status;

        public LoanRecord(int accountId, double amount, String loanType, String appliedDate, String status) {
            this.accountId = accountId;
            this.amount = amount;
            this.loanType = loanType;
            this.appliedDate = appliedDate;
            this.status = status;
        }

        public int getAccountId() { return accountId; }
        public double getAmount() { return amount; }
        public String getLoanType() { return loanType; }
        public String getAppliedDate() { return appliedDate; }
        public String getStatus() { return status; }
    }

    public List<LoanRecord> getLoans() {
        return new ArrayList<>(); // Placeholder
    }
}