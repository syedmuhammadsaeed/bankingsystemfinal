import javax.swing.*;
import bankingsystemfinal.Admin;
import bankingsystemfinal.LoginUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI(new Admin()).setVisible(true); // Updated to match constructor
        });
    }
}