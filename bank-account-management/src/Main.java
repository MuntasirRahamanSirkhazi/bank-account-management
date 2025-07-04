import gui.BankAppFrame;
import gui.LoginDialog;
import javax.swing.*;
import model.User;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isSucceeded()) {
                User user = login.getLoggedInUser();
                BankAppFrame frame = new BankAppFrame(user);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}