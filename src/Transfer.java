package bankingsystemfinal;

public class Transfer {
    private int accountIdFrom;
    private int accountIdTo;
    private double amount;
    private String timestamp;
    private String status;

    public Transfer(int accountIdFrom, int accountIdTo, double amount, String timestamp, String status) {
        this.accountIdFrom = accountIdFrom;
        this.accountIdTo = accountIdTo;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getAccountIdFrom() { return accountIdFrom; }
    public int getAccountIdTo() { return accountIdTo; }
    public double getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}