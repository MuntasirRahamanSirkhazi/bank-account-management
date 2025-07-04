package model;
import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String transactionId;
    private String accountId;
    private double amount;
    private String transactionType; // "Deposit", "Withdraw", "Transfer"
    private Date date;

    public Transaction(String transactionId, String accountId, double amount, String transactionType, Date date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.date = date;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return transactionType + " of " + amount + " on " + date;
    }
}