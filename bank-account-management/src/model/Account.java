package model;
import java.io.Serializable;

public class Account implements Serializable {
    private String accountId;
    private String ownerUsername; // Link to User
    private String accountType;   // e.g., "Savings", "Checking"
    private double balance;
    private String accountName;   // New field

    public Account(String accountId, String ownerUsername, String accountType, double balance, String accountName) {
        this.accountId = accountId;
        this.ownerUsername = ownerUsername;
        this.accountType = accountType;
        this.balance = balance;
        this.accountName = accountName;
    }

    // For backward compatibility
    public Account(String accountId, String ownerUsername, String accountType, double balance) {
        this(accountId, ownerUsername, accountType, balance, "");
    }

    public String getAccountId() { return accountId; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    // Interest calculation for savings accounts
    public void applyInterest(double rate) {
        if ("Savings".equalsIgnoreCase(accountType)) {
            balance += balance * rate;
        }
    }

    @Override
    public String toString() {
        return accountName + " | " + accountType + " | " + accountId + " | Balance: " + balance;
    }
}