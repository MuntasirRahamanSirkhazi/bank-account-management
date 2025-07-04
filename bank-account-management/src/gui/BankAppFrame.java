package gui;
import model.User;

import javax.swing.*;
import java.awt.*;

public class BankAppFrame extends JFrame {
    private User currentUser;

    public BankAppFrame(User user) {
        super("Bank Account Management System - User: " + user.getUsername());
        this.currentUser = user;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Account Management", new AccountFormPanel(currentUser));
        tabbedPane.addTab("Transaction Details", new TransactionDetailsPanel(currentUser));

        add(tabbedPane, BorderLayout.CENTER);
    }
}