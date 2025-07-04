package gui;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.swing.*;
import model.Account;
import model.Transaction;
import model.User;
import storage.AccountManager;
import storage.FileStorage;

public class AccountFormPanel extends JPanel {
    private User currentUser;
    private JComboBox<String> typeBox;
    private JTextField balanceField;
    private JTextField accountNameField; // New field for account name
    private DefaultListModel<Account> accountListModel;
    private JList<Account> accountList;

    public AccountFormPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // Top: Form
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5)); // 3 rows now
        form.add(new JLabel("Account Name:"));
        accountNameField = new JTextField();
        form.add(accountNameField);

        form.add(new JLabel("Account Type:"));
        typeBox = new JComboBox<>(new String[]{"Savings", "Current", "Fixed Deposit"});
        form.add(typeBox);

        form.add(new JLabel("Initial Balance:"));
        balanceField = new JTextField();
        form.add(balanceField);

        JButton createBtn = new JButton("Create Account");
        createBtn.addActionListener(e -> createAccount());

        JButton depositBtn = new JButton("Deposit");
        depositBtn.addActionListener(e -> depositOrWithdraw(true));

        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.addActionListener(e -> depositOrWithdraw(false));

        JButton deleteBtn = new JButton("Delete Account");
        deleteBtn.addActionListener(e -> deleteAccount());

        JButton transferBtn = new JButton("Transfer");
        transferBtn.addActionListener(e -> transferMoney());

        accountListModel = new DefaultListModel<>();
        accountList = new JList<>(accountListModel);
        refreshAccountList();

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(accountList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createBtn);
        bottomPanel.add(depositBtn);
        bottomPanel.add(withdrawBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(transferBtn);
    
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createAccount() {
        String accountName = accountNameField.getText().trim();
        if (accountName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String type = (String) typeBox.getSelectedItem();
        double balance;
        try {
            balance = Double.parseDouble(balanceField.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = String.format("%08d", new Random().nextInt(100_000_000));
        Account acc = new Account(id, currentUser.getUsername(), type, balance, accountName);
        List<Account> all = AccountManager.loadAccounts();
        all.add(acc);
        AccountManager.saveAccounts(all);

        // Record initial deposit as a transaction if balance > 0
        if (balance > 0) {
            List<Transaction> txns = FileStorage.loadTransactions();
            txns.add(new Transaction(
                UUID.randomUUID().toString(),
                id,
                balance,
                "Deposit",
                new Date()
            ));
            FileStorage.saveTransactions(txns);
        }

        refreshAccountList();
        JOptionPane.showMessageDialog(this, "Account created!\nAccount Number: " + id);
    }

    private void refreshAccountList() {
        accountListModel.clear();
        for (Account acc : AccountManager.getAccountsForUser(currentUser.getUsername())) {
            accountListModel.addElement(acc);
        }
    }

    private void transferMoney() {
        Account fromAccount = accountList.getSelectedValue();
        if (fromAccount == null) {
            JOptionPane.showMessageDialog(this, "Select your source account first.");
            return;
        }

        // Get all user accounts except the selected one
        List<Account> userAccounts = AccountManager.getAccountsForUser(currentUser.getUsername());
        DefaultComboBoxModel<Account> model = new DefaultComboBoxModel<>();
        for (Account acc : userAccounts) {
            if (!acc.getAccountId().equals(fromAccount.getAccountId())) {
                model.addElement(acc);
            }
        }
        if (model.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No other account to transfer to.");
            return;
        }

        JComboBox<Account> targetBox = new JComboBox<>(model);
        JTextField amountField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("To Account:"));
        panel.add(targetBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        Account toAccount = (Account) targetBox.getSelectedItem();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }
        if (fromAccount.getBalance() < amount) {
            JOptionPane.showMessageDialog(this, "Insufficient balance.");
            return;
        }

        // Update balances
        List<Account> all = AccountManager.loadAccounts();
        for (Account acc : all) {
            if (acc.getAccountId().equals(fromAccount.getAccountId())) {
                acc.setBalance(acc.getBalance() - amount);
            }
            if (acc.getAccountId().equals(toAccount.getAccountId())) {
                acc.setBalance(acc.getBalance() + amount);
            }
        }
        AccountManager.saveAccounts(all);

        // Record transactions for both accounts
        List<Transaction> txns = FileStorage.loadTransactions();
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        txns.add(new Transaction(uuid.toString(), fromAccount.getAccountId(), amount, "Transfer Out", now));
        txns.add(new Transaction(uuid.toString(), toAccount.getAccountId(), amount, "Transfer In", now));
        FileStorage.saveTransactions(txns);

        refreshAccountList();
        JOptionPane.showMessageDialog(this, "Transfer successful!");
    }

    private void deleteAccount() {
        Account selected = accountList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an account to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete account " + selected.getAccountId() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Remove account
        List<Account> all = AccountManager.loadAccounts();
        all.removeIf(acc -> acc.getAccountId().equals(selected.getAccountId()));
        AccountManager.saveAccounts(all);

        // Optionally, remove transactions for this account
        List<Transaction> txns = FileStorage.loadTransactions();
        txns.removeIf(txn -> txn.getAccountId().equals(selected.getAccountId()));
        FileStorage.saveTransactions(txns);

        refreshAccountList();
        JOptionPane.showMessageDialog(this, "Account deleted.");
    }

    private void depositOrWithdraw(boolean isDeposit) {
        Account selected = accountList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an account first.");
            return;
        }
        String action = isDeposit ? "Deposit" : "Withdraw";
        String input = JOptionPane.showInputDialog(this, "Enter amount to " + action.toLowerCase() + ":");
        if (input == null) return;
        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }
        if (!isDeposit && selected.getBalance() < amount) {
            JOptionPane.showMessageDialog(this, "Insufficient balance.");
            return;
        }
        List<Account> all = AccountManager.loadAccounts();
        for (Account acc : all) {
            if (acc.getAccountId().equals(selected.getAccountId())) {
                double newBalance = isDeposit ? acc.getBalance() + amount : acc.getBalance() - amount;
                acc.setBalance(newBalance);
                break;
            }
        }
        AccountManager.saveAccounts(all);

        // Record transaction
        List<Transaction> txns = FileStorage.loadTransactions();
        txns.add(new Transaction(
            UUID.randomUUID().toString(),
            selected.getAccountId(),
            amount,
            action,
            new Date()
        ));
        FileStorage.saveTransactions(txns);

        refreshAccountList();
        JOptionPane.showMessageDialog(this, action + " successful!");
    }

    
}