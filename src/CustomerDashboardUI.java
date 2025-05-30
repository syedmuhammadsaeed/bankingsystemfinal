package bankingsystemfinal;

import bankingsystemfinal.Account;
import bankingsystemfinal.AdminDashboard;
import bankingsystemfinal.CustomerDashboard;
import bankingsystemfinal.Loan;
import bankingsystemfinal.Transfer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomerDashboardUI extends JFrame {
    private CustomerDashboard dashboard;
    private int customerId;
    private JTextField amountField;
    private JTextField accountIdToField;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JLabel accountIdToLabel;
    private JButton actionButton;
    private JPanel bottomPanel;
    private JPanel transactionPanel;
    private JPanel loanPanel;
    private String currentTab = "Accounts";

    public CustomerDashboardUI(CustomerDashboard dashboard) {
        this.dashboard = dashboard;
        this.customerId = dashboard.getCustomerId();
        setTitle("Customer Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, width, height, 20, 20);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Left Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(33, 33, 33));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        String[] tabs = {"Accounts", "Transactions", "Deposit Request", "Loan Request", "Transfer", "Withdraw Request", "Notifications"};
        Color[] buttonColors = {
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156)
        };

        for (int i = 0; i < tabs.length; i++) {
            RoundedButton tabButton = new RoundedButton("  " + tabs[i]);
            final Color originalColor = buttonColors[i];
            final String tab = tabs[i];
            tabButton.setFont(new Font("Arial", Font.PLAIN, 16));
            tabButton.setForeground(Color.WHITE);
            tabButton.setBackground(originalColor);
            tabButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            tabButton.setHorizontalAlignment(SwingConstants.LEFT);
            tabButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            tabButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tabButton.setBackground(tabButton.getBackground().brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tabButton.setBackground(originalColor);
                }
            });
            tabButton.addActionListener(e -> updateTableForTab(tab));
            sidebarPanel.add(tabButton);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }

        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.addActionListener(e -> {
            dispose();
            new bankingsystemfinal.LoginUI(new bankingsystemfinal.Admin()).setVisible(true);
        });
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(5));

        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"Account ID", "Account Type", "Balance", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountsTable = new JTable(tableModel);
        accountsTable.setBackground(Color.WHITE);
        accountsTable.setForeground(Color.BLACK);
        accountsTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Dynamic Bottom Panel for Input Fields
        bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Amount input
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        accountIdToLabel = new JLabel("Account ID To:");
        accountIdToField = new JTextField(10);
        actionButton = new RoundedButton("Request Deposit");
        actionButton.setBackground(new Color(25, 118, 210));
        actionButton.setForeground(Color.WHITE);
        RoundedButton refreshButton = new RoundedButton("Refresh");
        refreshButton.setBackground(new Color(25, 118, 210));
        refreshButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(accountIdToLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(accountIdToField, gbc);
        accountIdToLabel.setVisible(false);
        accountIdToField.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        bottomPanel.add(actionButton, gbc);
        gbc.gridx = 1;
        bottomPanel.add(refreshButton, gbc);

        // Transaction-specific panel for Export button
        transactionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton exportTransactionButton = new RoundedButton("Export to PDF");
        exportTransactionButton.setBackground(new Color(25, 118, 210));
        exportTransactionButton.setForeground(Color.WHITE);
        exportTransactionButton.addActionListener(e -> exportTransactionsToPDF());
        transactionPanel.add(exportTransactionButton);
        transactionPanel.setVisible(false);

        // Loan-specific panel for Export button
        loanPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton exportLoanButton = new RoundedButton("Export to PDF");
        exportLoanButton.setBackground(new Color(25, 118, 210));
        exportLoanButton.setForeground(Color.WHITE);
        exportLoanButton.addActionListener(e -> exportLoansToPDF());
        loanPanel.add(exportLoanButton);
        loanPanel.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transactionPanel, BorderLayout.NORTH);
        topPanel.add(loanPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        actionButton.addActionListener(e -> {
            try {
                // Validate amount field
                if (amountField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Amount field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get first account automatically
                List<Account> accounts = dashboard.getAccounts();
                if (accounts.isEmpty()) {
                    JOptionPane.showMessageDialog(CustomerDashboardUI.this, "No accounts available. Please create an account first.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int accountId = accounts.get(0).getAccountId();

                if ("Deposit Request".equals(currentTab)) {
                    if (dashboard.deposit(accountId, amount)) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Deposit request successful!");
                        updateTableForTab(currentTab);
                    } else {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error depositing money: Database error or invalid account.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("Loan Request".equals(currentTab)) {
                    if (dashboard.requestLoan(accountId, amount)) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Loan request successful!");
                        updateTableForTab(currentTab);
                    } else {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error requesting loan: Database error or invalid account.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("Transfer".equals(currentTab)) {
                    if (accountIdToField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Account ID To field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int accountIdTo = Integer.parseInt(accountIdToField.getText().trim());
                    if (accountId == accountIdTo) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Cannot transfer to the same account.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (dashboard.transfer(accountId, accountIdTo, amount)) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Transfer request successful!");
                        updateTableForTab(currentTab);
                    } else {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error transferring money: Invalid accounts or database error.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ("Withdraw Request".equals(currentTab)) {
                    if (dashboard.withdraw(accountId, amount)) {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Withdraw request successful!");
                        updateTableForTab(currentTab);
                    } else {
                        JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error requesting withdraw: Database error or invalid account.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Invalid input: Amount and Account ID To (if applicable) must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error processing request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> {
            updateTableForTab(currentTab);
        });

        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        updateTableForTab("Accounts");
    }

    private void updateTableForTab(String tab) {
        currentTab = tab;
        tableModel.setRowCount(0);
        bottomPanel.setVisible(true);
        transactionPanel.setVisible(false);
        loanPanel.setVisible(false);

        switch (tab) {
            case "Accounts":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Account Type", "Balance", "Status"});
                List<Account> accounts = dashboard.getAccounts();
                for (Account account : accounts) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", account.getAccountId() % 10000),
                            account.getAccountType(),
                            String.format("%.2f", account.getBalance()),
                            "Active"
                    });
                }
                bottomPanel.setVisible(false);
                break;

            case "Transactions":
                tableModel.setColumnIdentifiers(new String[]{"Account ID From", "Account ID To", "Type", "Amount", "Timestamp", "Status"});
                List<CustomerDashboard.TransactionRecord> transactions = dashboard.getTransactions();
                for (CustomerDashboard.TransactionRecord transaction : transactions) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", transaction.getAccountIdFrom() % 10000),
                            transaction.getAccountIdTo() == 0 ? "-" : "****" + String.format("%04d", transaction.getAccountIdTo() % 10000),
                            transaction.getType(),
                            String.format("%.2f", transaction.getAmount()),
                            transaction.getTimestamp(),
                            transaction.getStatus()
                    });
                }
                bottomPanel.setVisible(false);
                transactionPanel.setVisible(true);
                break;

            case "Deposit Request":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Amount", "Timestamp", "Status"});
                List<AdminDashboard.DepositRequest> depositRequests = dashboard.getDepositRequests();
                for (AdminDashboard.DepositRequest request : depositRequests) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", request.getAccountId() % 10000),
                            String.format("%.2f", request.getAmount()),
                            request.getTimestamp(),
                            request.getStatus()
                    });
                }
                accountIdToLabel.setVisible(false);
                accountIdToField.setVisible(false);
                actionButton.setText("Request Deposit");
                break;

            case "Loan Request":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Amount", "Loan Type", "Applied Date", "Status"});
                List<Loan.LoanRecord> loanRequests = dashboard.getLoanRequests();
                for (Loan.LoanRecord loan : loanRequests) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", loan.getAccountId() % 10000),
                            String.format("%.2f", loan.getAmount()),
                            loan.getLoanType(),
                            loan.getAppliedDate(),
                            loan.getStatus()
                    });
                }
                accountIdToLabel.setVisible(false);
                accountIdToField.setVisible(false);
                actionButton.setText("Request Loan");
                loanPanel.setVisible(true);
                break;

            case "Transfer":
                tableModel.setColumnIdentifiers(new String[]{"Account ID From", "Account ID To", "Amount", "Timestamp", "Status"});
                List<Transfer> transfers = dashboard.getTransfers();
                for (Transfer transfer : transfers) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", transfer.getAccountIdFrom() % 10000),
                            "****" + String.format("%04d", transfer.getAccountIdTo() % 10000),
                            String.format("%.2f", transfer.getAmount()),
                            transfer.getTimestamp(),
                            transfer.getStatus()
                    });
                }
                accountIdToLabel.setVisible(true);
                accountIdToField.setVisible(true);
                actionButton.setText("Request Transfer");
                break;

            case "Withdraw Request":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Amount", "Timestamp", "Status"});
                List<AdminDashboard.WithdrawRequest> withdrawRequests = dashboard.getWithdrawRequests();
                for (AdminDashboard.WithdrawRequest request : withdrawRequests) {
                    tableModel.addRow(new Object[]{
                            "****" + String.format("%04d", request.getAccountId() % 10000),
                            String.format("%.2f", request.getAmount()),
                            request.getTimestamp(),
                            request.getStatus()
                    });
                }
                accountIdToLabel.setVisible(false);
                accountIdToField.setVisible(false);
                actionButton.setText("Request Withdraw");
                break;

            case "Notifications":
                tableModel.setColumnIdentifiers(new String[]{"ID", "Message", "Timestamp"});
                List<CustomerDashboard.Notification> notifications = dashboard.getNotifications();
                for (CustomerDashboard.Notification notification : notifications) {
                    tableModel.addRow(new Object[]{
                            notification.getId(),
                            notification.getMessage(),
                            notification.getTimestamp()
                    });
                }
                bottomPanel.setVisible(false);
                break;
        }
    }

    private void exportTransactionsToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Transaction_History_" + customerId + ".pdf"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 PdfWriter writer = new PdfWriter(fos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph("Transaction History for Customer ID: " + customerId));
                document.add(new Paragraph("Date: " + java.time.LocalDateTime.now()));

                Table table = new Table(6);
                table.addCell("Account ID From");
                table.addCell("Account ID To");
                table.addCell("Type");
                table.addCell("Amount");
                table.addCell("Timestamp");
                table.addCell("Status");

                for (CustomerDashboard.TransactionRecord transaction : dashboard.getTransactions()) {
                    table.addCell("****" + String.format("%04d", transaction.getAccountIdFrom() % 10000));
                    table.addCell(transaction.getAccountIdTo() == 0 ? "-" : "****" + String.format("%04d", transaction.getAccountIdTo() % 10000));
                    table.addCell(transaction.getType());
                    table.addCell(String.format("%.2f", transaction.getAmount()));
                    table.addCell(transaction.getTimestamp());
                    table.addCell(transaction.getStatus());
                }

                document.add(table);
                JOptionPane.showMessageDialog(this, "PDF exported successfully to " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportLoansToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Loan_Details_" + customerId + "_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 PdfWriter writer = new PdfWriter(fos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph("Loan Details for Customer ID: " + customerId));
                document.add(new Paragraph("Date: " + java.time.LocalDateTime.now()));

                Table table = new Table(5);
                table.addCell("Account ID");
                table.addCell("Amount");
                table.addCell("Loan Type");
                table.addCell("Applied Date");
                table.addCell("Status");

                for (Loan.LoanRecord loan : dashboard.getLoanRequests()) {
                    table.addCell("****" + String.format("%04d", loan.getAccountId() % 10000));
                    table.addCell(String.format("%.2f", loan.getAmount()));
                    table.addCell(loan.getLoanType());
                    table.addCell(loan.getAppliedDate());
                    table.addCell(loan.getStatus());
                }

                document.add(table);
                JOptionPane.showMessageDialog(this, "PDF exported successfully to " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboardUI(new CustomerDashboard(1)).setVisible(true));
    }
}