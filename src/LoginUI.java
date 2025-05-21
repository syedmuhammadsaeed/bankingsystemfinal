package bankingsystemfinal;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import bankingsystemfinal.AdminDashboardUI;
import bankingsystemfinal.Admin;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton customerRadio;
    private JRadioButton adminRadio;
    private bankingsystemfinal.Admin admin;

    public LoginUI(bankingsystemfinal.Admin admin) {
        this.admin = admin != null ? admin : new Admin();
        setTitle("FutureBank - Login");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove default window borders for custom styling
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Futuristic gradient background panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(25, 20, 50); // Deep blue-purple
                Color color2 = new Color(75, 50, 100); // Lighter purple
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Add a subtle glowing accent line
                g2d.setColor(new Color(0, 255, 150, 100)); // Neon green with transparency
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(20, h - 50, w - 20, h - 50);
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Main login panel with glassmorphism effect
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(255, 255, 255, 200)); // Translucent white
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 150, 50), 2, true), // Neon green outline
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        loginPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("FutureBank Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 255, 150)); // Neon green
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(titleLabel, gbc);

        // Email Label and Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        emailField.setBackground(new Color(255, 255, 255, 180));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(emailField, gbc);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(new Color(255, 255, 255, 180));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        // Role Selection
        ButtonGroup roleGroup = new ButtonGroup();
        customerRadio = new JRadioButton("Customer");
        adminRadio = new JRadioButton("Admin");
        customerRadio.setFont(new Font("Arial", Font.PLAIN, 16));
        adminRadio.setFont(new Font("Arial", Font.PLAIN, 16));
        customerRadio.setForeground(Color.WHITE);
        adminRadio.setForeground(Color.WHITE);
        customerRadio.setOpaque(false);
        adminRadio.setOpaque(false);
        roleGroup.add(customerRadio);
        roleGroup.add(adminRadio);
        customerRadio.setSelected(true);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rolePanel.setOpaque(false);
        rolePanel.add(customerRadio);
        rolePanel.add(adminRadio);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(rolePanel, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 150, 255));
                loginButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 150), 3, true),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 120, 215));
                loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (customerRadio.isSelected()) {
                    if (Customer.validateCustomerLogin(email, password)) {
                        int customerId = Customer.getCustomerId(email);
                        if (customerId != -1) {
                            new bankingsystemfinal.CustomerDashboardUI(new bankingsystemfinal.CustomerDashboard(customerId)).setVisible(true);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Invalid email or password.");
                    }
                } else if (adminRadio.isSelected()) {
                    if (admin.validateAdminLogin(email, password)) {
                        int adminId = admin.getAdminId(email);
                        if (adminId != -1) {
                            bankingsystemfinal.Admin.AdminRecord adminRecord = new bankingsystemfinal.Admin.AdminRecord(adminId, email);
                            new AdminDashboardUI(new bankingsystemfinal.AdminDashboard()).setVisible(true);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Invalid email or password.");
                    }
                }
            }
        });

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(0, 180, 100));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(0, 200, 120));
                registerButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 150), 3, true),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(0, 180, 100));
                registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        loginPanel.add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a custom registration dialog
                JDialog registerDialog = new JDialog(LoginUI.this, "Register", true);
                registerDialog.setLayout(new BorderLayout());
                registerDialog.setSize(400, 400); // Adjusted height for compact layout
                registerDialog.setLocationRelativeTo(LoginUI.this);
                registerDialog.setUndecorated(true);

                // Glassmorphism panel for registration
                JPanel dialogPanel = new JPanel(new GridBagLayout());
                dialogPanel.setBackground(new Color(240, 240, 240, 200)); // Light gray with transparency
                dialogPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 50), 2, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Reduced overall padding
                ));
                dialogPanel.setOpaque(false);

                GridBagConstraints dialogGbc = new GridBagConstraints();
                dialogGbc.insets = new Insets(5, 10, 5, 10); // Reduced spacing between fields
                dialogGbc.fill = GridBagConstraints.HORIZONTAL;
                dialogGbc.anchor = GridBagConstraints.CENTER;

                JLabel regTitleLabel = new JLabel("Register");
                regTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                regTitleLabel.setForeground(new Color(0, 255, 150)); // Neon green
                dialogGbc.gridwidth = 2;
                dialogGbc.gridx = 0;
                dialogGbc.gridy = 0;
                dialogPanel.add(regTitleLabel, dialogGbc);

                JTextField nameField = new JTextField(20);
                nameField.setFont(new Font("Arial", Font.PLAIN, 16));
                nameField.setBackground(new Color(255, 255, 255, 180));
                nameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                JTextField emailFieldReg = new JTextField(20);
                emailFieldReg.setFont(new Font("Arial", Font.PLAIN, 16));
                emailFieldReg.setBackground(new Color(255, 255, 255, 180));
                emailFieldReg.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                JPasswordField passwordFieldReg = new JPasswordField(20);
                passwordFieldReg.setFont(new Font("Arial", Font.PLAIN, 16));
                passwordFieldReg.setBackground(new Color(255, 255, 255, 180));
                passwordFieldReg.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                JTextField phoneField = new JTextField(20);
                phoneField.setFont(new Font("Arial", Font.PLAIN, 16));
                phoneField.setBackground(new Color(255, 255, 255, 180));
                phoneField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
                accountTypeCombo.setFont(new Font("Arial", Font.PLAIN, 16));
                accountTypeCombo.setBackground(new Color(255, 255, 255, 180));
                accountTypeCombo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 150, 100), 2, true),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

                JLabel nameLabel = new JLabel("Name:");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                nameLabel.setForeground(Color.BLACK);
                JLabel emailLabelReg = new JLabel("Email:");
                emailLabelReg.setFont(new Font("Arial", Font.PLAIN, 16));
                emailLabelReg.setForeground(Color.BLACK);
                JLabel passwordLabelReg = new JLabel("Password:");
                passwordLabelReg.setFont(new Font("Arial", Font.PLAIN, 16));
                passwordLabelReg.setForeground(Color.BLACK);
                JLabel phoneLabel = new JLabel("Phone Number:");
                phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                phoneLabel.setForeground(Color.BLACK);
                JLabel accountTypeLabel = new JLabel("Account Type:");
                accountTypeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                accountTypeLabel.setForeground(Color.BLACK);

                dialogGbc.gridwidth = 1;
                dialogGbc.gridx = 0;
                dialogGbc.gridy = 1;
                dialogPanel.add(nameLabel, dialogGbc);
                dialogGbc.gridx = 1;
                dialogPanel.add(nameField, dialogGbc);

                dialogGbc.gridx = 0;
                dialogGbc.gridy = 2;
                dialogPanel.add(emailLabelReg, dialogGbc);
                dialogGbc.gridx = 1;
                dialogPanel.add(emailFieldReg, dialogGbc);

                dialogGbc.gridx = 0;
                dialogGbc.gridy = 3;
                dialogPanel.add(passwordLabelReg, dialogGbc);
                dialogGbc.gridx = 1;
                dialogPanel.add(passwordFieldReg, dialogGbc);

                dialogGbc.gridx = 0;
                dialogGbc.gridy = 4;
                dialogPanel.add(phoneLabel, dialogGbc);
                dialogGbc.gridx = 1;
                dialogPanel.add(phoneField, dialogGbc);

                dialogGbc.gridx = 0;
                dialogGbc.gridy = 5;
                dialogPanel.add(accountTypeLabel, dialogGbc);
                dialogGbc.gridx = 1;
                dialogPanel.add(accountTypeCombo, dialogGbc);

                JButton submitButton = new JButton("Submit");
                submitButton.setFont(new Font("Arial", Font.BOLD, 16));
                submitButton.setBackground(new Color(0, 180, 100));
                submitButton.setForeground(Color.WHITE);
                submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                submitButton.setFocusPainted(false);
                submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        submitButton.setBackground(new Color(0, 200, 120));
                        submitButton.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(0, 255, 150, 150), 3, true),
                                BorderFactory.createEmptyBorder(8, 18, 8, 18)
                        ));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        submitButton.setBackground(new Color(0, 180, 100));
                        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    }
                });
                dialogGbc.gridx = 0;
                dialogGbc.gridy = 6;
                dialogGbc.gridwidth = 2;
                dialogPanel.add(submitButton, dialogGbc);

                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        String name = nameField.getText();
                        String email = emailFieldReg.getText();
                        String password = new String(passwordFieldReg.getPassword());
                        String phoneNumber = phoneField.getText();
                        String accountType = (String) accountTypeCombo.getSelectedItem();

                        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                            JOptionPane.showMessageDialog(registerDialog, "All fields are required.");
                            return;
                        }

                        if (Customer.registerCustomer(name, email, password, phoneNumber)) {
                            int customerId = Customer.getCustomerId(email);
                            if (customerId != -1) {
                                if (createAccount(customerId, accountType)) {
                                    JOptionPane.showMessageDialog(registerDialog, "Registration and account creation successful!");
                                    registerDialog.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(registerDialog, "Registration succeeded, but account creation failed. Check console for details.");
                                    registerDialog.dispose();
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(registerDialog, "Registration failed. Email may already exist.");
                        }
                    }
                });

                registerDialog.add(dialogPanel, BorderLayout.CENTER);
                JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                closePanel.setOpaque(false);
                JButton closeDialogButton = new JButton("X");
                closeDialogButton.setFont(new Font("Arial", Font.BOLD, 14));
                closeDialogButton.setForeground(Color.WHITE);
                closeDialogButton.setBackground(new Color(255, 99, 71));
                closeDialogButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                closeDialogButton.setFocusPainted(false);
                closeDialogButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                closeDialogButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        registerDialog.dispose();
                    }
                });
                closePanel.add(closeDialogButton);
                registerDialog.add(closePanel, BorderLayout.NORTH);
                registerDialog.setVisible(true);
            }
        });

        gradientPanel.add(loginPanel, BorderLayout.CENTER);
        add(gradientPanel, BorderLayout.CENTER);

        // Custom close button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 99, 71));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });
        topPanel.add(closeButton);
        add(topPanel, BorderLayout.NORTH);
    }

    private boolean createAccount(int customerId, String accountType) {
        if (!accountType.equals("Savings") && !accountType.equals("Current")) {
            System.err.println("Invalid account type. Must be 'Savings' or 'Current'.");
            return false;
        }

        Connection conn = bankingsystemfinal.DBConnection.getConnection();
        if (conn == null) {
            System.err.println("Failed to create account: Database connection is null.");
            return false;
        }

        try {
            String sql = "INSERT INTO accounts (customer_id, account_type, balance) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, accountType);
            pstmt.setDouble(3, 0.0); // Initial balance is 0
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginUI(new Admin());
            }
        });
    }
}