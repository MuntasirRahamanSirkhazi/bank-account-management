package storage;
import model.Transaction;
import java.util.*;
import java.io.*;

public class FileStorage {
    private static final String FILE_NAME = "transactions.dat";

    public static List<Transaction> loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Transaction>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveTransactions(List<Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> getTransactionsForAccount(String accountId) {
        List<Transaction> all = loadTransactions();
        List<Transaction> accountTxns = new ArrayList<>();
        for (Transaction txn : all) {
            if (txn.getAccountId().equals(accountId)) {
                accountTxns.add(txn);
            }
        }
        return accountTxns;
    }
}