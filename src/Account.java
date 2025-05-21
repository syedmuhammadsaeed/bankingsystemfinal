package bankingsystemfinal; // Adjust package if different

public class Account {
    private int accountId;
    private String accountType;
    private double balance;

    public Account(int accountId, String accountType, double balance) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public int getAccountId() { return accountId; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
}