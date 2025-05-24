package bankingsystemfinal;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.geom.RoundRectangle2D;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton customerRadio;
    private JRadioButton adminRadio;
    private bankingsystemfinal.Admin admin;

    public LoginUI(bankingsystemfinal.Admin admin) {
        this.admin = admin != null ? admin : new bankingsystemfinal.Admin();
        setTitle("FutureBank - Login");
        setSize(500, 650); // Increased height for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30)); // Rounded corners
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Futuristic background with animated particles
        JPanel backgroundPanel = new JPanel() {
            private final int PARTICLE_COUNT = 50;
            private final Point[] particles = new Point[PARTICLE_COUNT];
            private final Color[] particleColors = new Color[PARTICLE_COUNT];

            {
                // Initialize particles
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    particles[i] = new Point(
                            (int)(Math.random() * getWidth()),
                            (int)(Math.random() * getHeight())
                    );
                    particleColors[i] = new Color(
                            0, 255, 150, (int)(Math.random() * 55 + 50)
                    );
                }

                // Animation timer
                new Timer(30, e -> repaint()).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark gradient background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 15, 30),
                        getWidth(), getHeight(), new Color(5, 10, 20)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw connecting lines between particles
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                        double dist = particles[i].distance(particles[j]);
                        if (dist < 150) {
                            int alpha = (int)(255 - (dist * 1.7));
                            if (alpha > 0) {
                                g2d.setColor(new Color(0, 255, 150, alpha / 2));
                                g2d.drawLine(particles[i].x, particles[i].y, particles[j].x, particles[j].y);
                            }
                        }
                    }
                }

                // Draw particles
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    // Update particle position
                    particles[i].x += (int)(Math.random() * 5 - 2);
                    particles[i].y += (int)(Math.random() * 5 - 2);

                    // Wrap around screen edges
                    if (particles[i].x < 0) particles[i].x = getWidth();
                    if (particles[i].x > getWidth()) particles[i].x = 0;
                    if (particles[i].y < 0) particles[i].y = getHeight();
                    if (particles[i].y > getHeight()) particles[i].y = 0;

                    // Draw particle
                    g2d.setColor(particleColors[i]);
                    g2d.fillOval(particles[i].x, particles[i].y, 3, 3);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Glassmorphism panel for login form
        JPanel loginPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a glass effect with blur
                int blurRadius = 20;
                int shadowOffset = 5;
                int arc = 25;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(
                        shadowOffset, shadowOffset,
                        getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2,
                        arc, arc
                );

                // Glass panel
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(
                        0, 0,
                        getWidth() - shadowOffset, getHeight() - shadowOffset,
                        arc, arc
                );

                // Border
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.drawRoundRect(
                        0, 0,
                        getWidth() - shadowOffset, getHeight() - shadowOffset,
                        arc, arc
                );

                g2d.dispose();
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title with futuristic font
        JLabel titleLabel = new JLabel("FUTUREBANK");
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/futuristic.ttf"))
                    .deriveFont(Font.BOLD, 36f);
            titleLabel.setFont(customFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Fallback
        }
        titleLabel.setForeground(new Color(0, 255, 180));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Quantum Financial Gateway");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        loginPanel.add(subtitleLabel, gbc);

        // Email Field
        emailField = createFuturisticTextField("Email Address");
        gbc.gridy = 2;
        loginPanel.add(emailField, gbc);

        // Password Field
        passwordField = createFuturisticPasswordField("Password");
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);

        // Role Selection with modern toggle
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        rolePanel.setOpaque(false);

        ButtonGroup roleGroup = new ButtonGroup();
        customerRadio = createToggleButton("CUSTOMER", true);
        adminRadio = createToggleButton("ADMIN", false);

        roleGroup.add(customerRadio);
        roleGroup.add(adminRadio);

        rolePanel.add(customerRadio);
        rolePanel.add(adminRadio);

        gbc.gridy = 4;
        loginPanel.add(rolePanel, gbc);

        // Login Button with hover effect
        JButton loginButton = new JButton("AUTHENTICATE");
        styleFuturisticButton(loginButton);
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 5;
        loginPanel.add(loginButton, gbc);

        // Register Button
        JButton registerButton = new JButton("NEW USER? REGISTER");
        registerButton.setForeground(new Color(200, 200, 200));
        registerButton.setContentAreaFilled(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> showRegistrationDialog());
        gbc.gridy = 6;
        loginPanel.add(registerButton, gbc);

        // Add components to background
        backgroundPanel.add(loginPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);

        // Custom window controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);

        JButton minimizeButton = createControlButton("−", new Color(255, 204, 0));
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        JButton closeButton = createControlButton("×", new Color(255, 50, 50));
        closeButton.addActionListener(e -> System.exit(0));

        controlPanel.add(minimizeButton);
        controlPanel.add(closeButton);

        add(controlPanel, BorderLayout.NORTH);
    }

    private JTextField createFuturisticTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 255, 180));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 255, 180, 100)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Placeholder effect
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private JPasswordField createFuturisticPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0, 255, 180));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 255, 180, 100)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Placeholder effect
        field.setEchoChar((char)0);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char)0);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private JRadioButton createToggleButton(String text, boolean selected) {
        JRadioButton button = new JRadioButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(selected ? new Color(0, 255, 180) : new Color(150, 150, 150));
        button.setOpaque(false);
        button.setSelected(selected);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addItemListener(e -> {
            button.setForeground(button.isSelected() ? new Color(0, 255, 180) : new Color(150, 150, 150));
        });

        return button;
    }

    private void styleFuturisticButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 180, 130));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 180, 100), 2),
                BorderFactory.createEmptyBorder(15, 40, 15, 40)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 220, 160));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 180), 2),
                        BorderFactory.createEmptyBorder(15, 40, 15, 40)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 180, 130));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 180, 100), 2),
                        BorderFactory.createEmptyBorder(15, 40, 15, 40)
                ));
            }
        });
    }

    private JButton createControlButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Skip if fields contain placeholder text
        if (email.equals("Email Address") || new String(passwordField.getPassword()).equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please enter your credentials", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (customerRadio.isSelected()) {
            if (bankingsystemfinal.Customer.validateCustomerLogin(email, password)) {
                int customerId = bankingsystemfinal.Customer.getCustomerId(email);
                if (customerId != -1) {
                    new bankingsystemfinal.CustomerDashboardUI(new bankingsystemfinal.CustomerDashboard(customerId)).setVisible(true);
                    dispose();
                }
            } else {

                JOptionPane.showMessageDialog(this, "Invalid email or password", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (adminRadio.isSelected()) {
            if (admin.validateAdminLogin(email, password)) {
                int adminId = admin.getAdminId(email);
                if (adminId != -1) {
                    bankingsystemfinal.Admin.AdminRecord adminRecord = new bankingsystemfinal.Admin.AdminRecord(adminId, email);
                    new bankingsystemfinal.AdminDashboardUI(new bankingsystemfinal.AdminDashboard()).setVisible(true);
                    dispose();
                }
            } else {

                JOptionPane.showMessageDialog(this, "Invalid email or password", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void showRegistrationDialog() {
        JDialog registerDialog = new JDialog(this, "Register", true);
        registerDialog.setSize(450, 600);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setUndecorated(true);
        registerDialog.setShape(new RoundRectangle2D.Double(0, 0, 450, 600, 30, 30));

        // Glass panel for dialog
        JPanel dialogPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass effect
                g2d.setColor(new Color(30, 35, 50, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Border
                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(new Color(0, 255, 180, 100));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2d.dispose();
            }
        };
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/futuristic.ttf"))
                    .deriveFont(Font.BOLD, 24f);
            titleLabel.setFont(customFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Fallback
        }
        titleLabel.setForeground(new Color(0, 255, 180));
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        dialogPanel.add(titleLabel, gbc);

        // Form fields
        JTextField nameField = createFuturisticTextField("Full Name");
        gbc.gridy = 1;
        dialogPanel.add(nameField, gbc);

        JTextField emailField = createFuturisticTextField("Email Address");
        gbc.gridy = 2;
        dialogPanel.add(emailField, gbc);

        JPasswordField passwordField = createFuturisticPasswordField("Password");
        gbc.gridy = 3;
        dialogPanel.add(passwordField, gbc);

        JTextField phoneField = createFuturisticTextField("Phone Number");
        gbc.gridy = 4;
        dialogPanel.add(phoneField, gbc);

        // Account type selector
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
        accountTypeCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        accountTypeCombo.setForeground(Color.WHITE);
        accountTypeCombo.setBackground(new Color(30, 35, 50));
        accountTypeCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 180, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 5;
        dialogPanel.add(accountTypeCombo, gbc);

        // Submit button
        JButton submitButton = new JButton("REGISTER ACCOUNT");
        styleFuturisticButton(submitButton);
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String phone = phoneField.getText();
            String accountType = (String) accountTypeCombo.getSelectedItem();

            // Validate inputs
            if (name.isEmpty() || name.equals("Full Name") ||
                    email.isEmpty() || email.equals("Email Address") ||
                    password.isEmpty() || new String(passwordField.getPassword()).equals("Password") ||
                    phone.isEmpty() || phone.equals("Phone Number")) {

                JOptionPane.showMessageDialog(registerDialog,
                        "All fields are required",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (bankingsystemfinal.Customer.registerCustomer(name, email, password, phone)) {
                int customerId = bankingsystemfinal.Customer.getCustomerId(email);
                if (customerId != -1) {
                    if (createAccount(customerId, accountType)) {
                        JOptionPane.showMessageDialog(registerDialog,
                                "Registration successful!\nYour account has been created.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        registerDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(registerDialog,
                                "Account creation failed",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(registerDialog,
                        "Registration failed. Email may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 6;
        dialogPanel.add(submitButton, gbc);

        // Close button
        JButton closeButton = new JButton("CLOSE");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setForeground(new Color(200, 200, 200));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> registerDialog.dispose());
        gbc.gridy = 7;
        dialogPanel.add(closeButton, gbc);

        registerDialog.add(dialogPanel);
        registerDialog.setVisible(true);
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
        SwingUtilities.invokeLater(() -> {
            try {
                // Set modern look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and show the login UI
                LoginUI loginUI = new LoginUI(new bankingsystemfinal.Admin());
                loginUI.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}