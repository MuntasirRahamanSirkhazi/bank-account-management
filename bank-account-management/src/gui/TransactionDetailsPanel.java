package gui;
import model.Transaction;
import model.User;
import model.Account;
import storage.AccountManager;
import storage.FileStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionDetailsPanel extends JPanel {
    private User currentUser;
    private JComboBox<Account> accountComboBox;
    private DefaultTableModel tableModel;

    public TransactionDetailsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // Account selection
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Account:"));
        accountComboBox = new JComboBox<>();
        refreshAccounts();
        topPanel.add(accountComboBox);

        JButton loadBtn = new JButton("Load Transactions");
        loadBtn.addActionListener(e -> loadTransactions());
        topPanel.add(loadBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table for transactions
        String[] columns = {"Transaction ID", "Amount", "Type", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refreshAccounts() {
        accountComboBox.removeAllItems();
        List<Account> accounts = AccountManager.getAccountsForUser(currentUser.getUsername());
        for (Account acc : accounts) {
            accountComboBox.addItem(acc);
        }
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        Account selected = (Account) accountComboBox.getSelectedItem();
        if (selected == null) return;
        List<Transaction> txns = FileStorage.getTransactionsForAccount(selected.getAccountId());
        for (Transaction txn : txns) {
            tableModel.addRow(new Object[]{
                txn.getTransactionId(),
                txn.getAmount(),
                txn.getTransactionType(),
                txn.getDate()
            });

        }}}