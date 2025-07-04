package gui;
import model.User;
import storage.UserStorage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean succeeded;
    private User loggedInUser;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        setLayout(new GridLayout(3, 2, 5, 5));
        add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        add(usernameField);
        add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            List<User> users = UserStorage.loadUsers();
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    succeeded = true;
                    loggedInUser = user;
                    dispose();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            succeeded = false;
        });

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<User> users = UserStorage.loadUsers();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            User newUser = new User(username, password);
            users.add(newUser);
            UserStorage.saveUsers(users);
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
        });

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() { return succeeded; }
    public User getLoggedInUser() { return loggedInUser;}
}