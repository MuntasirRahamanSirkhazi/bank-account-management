package storage;
import model.Account;
import java.util.*;
import java.io.*;

public class AccountManager {
    private static final String FILE_NAME = "accounts.dat";

    public static List<Account> loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Account>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveAccounts(List<Account> accounts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Account> getAccountsForUser(String username) {
        List<Account> all = loadAccounts();
        List<Account> userAccounts = new ArrayList<>();
        for (Account acc : all) {
            if (acc.getOwnerUsername().equals(username)) {
                userAccounts.add(acc);
            }
        }
        return userAccounts;
    }
}