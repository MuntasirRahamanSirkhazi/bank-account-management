package model;
import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password; // For demo only; use hashing in real apps!

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}