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
import java.awt.event.ActionListener;
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
    private JTextField accountIdField;
    private JTextField amountField;
    private JTextField accountIdToField;
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JLabel accountIdToLabel;
    private JButton actionButton;
    private JPanel bottomPanel;
    private JPanel transactionPanel;
    private JPanel loanPanel; // New panel for loan export button
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

    private void initComponents() {
        setLayout(new BorderLayout());

        // Left Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(33, 33, 33));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        String[] tabs = {"Accounts", "Transactions", "Deposit Request", "Loan Request", "Transfer", "Withdraw Request", "Notifications"};
        for (String tab : tabs) {
            JButton tabButton = new JButton("  " + tab);
            tabButton.setFont(new Font("Arial", Font.PLAIN, 16));
            tabButton.setForeground(Color.WHITE);
            tabButton.setBackground(new Color(33, 33, 33));
            tabButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            tabButton.setHorizontalAlignment(SwingConstants.LEFT);
            tabButton.setFocusPainted(false);
            tabButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentTab = tab;
                    updateTableForTab(tab);
                }
            });
            sidebarPanel.add(tabButton);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 99, 71));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new bankingsystemfinal.LoginUI(new bankingsystemfinal.Admin()).setVisible(true);
            }
        });
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(5));

        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Center Table
        String[] initialColumns = {"Account ID", "Account Type", "Balance", "Status"};
        tableModel = new DefaultTableModel(initialColumns, 0) {
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

        JLabel accountIdLabel = new JLabel("Account ID:");
        accountIdField = new JTextField(10);
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        accountIdToLabel = new JLabel("Account ID To:");
        accountIdToField = new JTextField(10);
        actionButton = new JButton("Request Deposit");
        JButton refreshButton = new JButton("Refresh");

        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(accountIdLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(accountIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        bottomPanel.add(accountIdToLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(accountIdToField, gbc);
        accountIdToLabel.setVisible(false);
        accountIdToField.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        bottomPanel.add(actionButton, gbc);
        gbc.gridx = 1;
        bottomPanel.add(refreshButton, gbc);

        // Transaction-specific panel for Export button
        transactionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportTransactionButton = new JButton("Export to PDF");
        exportTransactionButton.setBackground(new Color(25, 118, 210));
        exportTransactionButton.setForeground(Color.WHITE);
        exportTransactionButton.setFocusPainted(false);
        exportTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportTransactionsToPDF();
            }
        });
        transactionPanel.add(exportTransactionButton);
        transactionPanel.setVisible(false);

        // Loan-specific panel for Export button
        loanPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportLoanButton = new JButton("Export to PDF");
        exportLoanButton.setBackground(new Color(25, 118, 210));
        exportLoanButton.setForeground(Color.WHITE);
        exportLoanButton.setFocusPainted(false);
        exportLoanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportLoansToPDF();
            }
        });
        loanPanel.add(exportLoanButton);
        loanPanel.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(transactionPanel, BorderLayout.NORTH);
        topPanel.add(loanPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountId = Integer.parseInt(accountIdField.getText());
                    double amount = Double.parseDouble(amountField.getText());
                    if ("Deposit Request".equals(currentTab)) {
                        if (dashboard.deposit(accountId, amount)) {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Deposit request successful!");
                            updateTableForTab(currentTab);
                        } else {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error depositing money: Account not found or does not belong to this customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if ("Loan Request".equals(currentTab)) {
                        if (dashboard.requestLoan(accountId, amount)) {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Loan request successful!");
                            updateTableForTab(currentTab);
                        } else {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error requesting loan: Account not found or does not belong to this customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if ("Transfer".equals(currentTab)) {
                        int accountIdTo = Integer.parseInt(accountIdToField.getText());
                        if (dashboard.transfer(accountId, accountIdTo, amount)) {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Transfer request successful!");
                            updateTableForTab(currentTab);
                        } else {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error transferring money: Invalid accounts or insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if ("Withdraw Request".equals(currentTab)) {
                        if (dashboard.withdraw(accountId, amount)) {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Withdraw request successful!");
                            updateTableForTab(currentTab);
                        } else {
                            JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Error requesting withdraw: Account not found or does not belong to this customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CustomerDashboardUI.this, "Invalid input. All fields must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTableForTab(currentTab);
            }
        });

        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        updateTableForTab("Accounts"); // Initial data load for Accounts tab
    }

    private void updateTableForTab(String tab) {
        tableModel.setRowCount(0); // Clear existing rows
        bottomPanel.setVisible(true); // Show bottom panel by default
        transactionPanel.setVisible(false); // Hide transaction panel by default
        loanPanel.setVisible(false); // Hide loan panel by default

        switch (tab) {
            case "Accounts":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Account Type", "Balance", "Status"});
                List<Account> accounts = dashboard.getAccounts();
                for (Account account : accounts) {
                    tableModel.addRow(new Object[]{
                            account.getAccountId(),
                            account.getAccountType(),
                            account.getBalance(),
                            "Active"
                    });
                }
                bottomPanel.setVisible(false); // Hide bottom panel for Accounts
                break;

            case "Transactions":
                tableModel.setColumnIdentifiers(new String[]{"Account ID From", "Account ID To", "Type", "Amount", "Timestamp", "Status"});
                List<CustomerDashboard.TransactionRecord> transactions = dashboard.getTransactions();
                for (CustomerDashboard.TransactionRecord transaction : transactions) {
                    tableModel.addRow(new Object[]{
                            transaction.getAccountIdFrom(),
                            transaction.getAccountIdTo() == 0 ? "-" : transaction.getAccountIdTo(),
                            transaction.getType(),
                            transaction.getAmount(),
                            transaction.getTimestamp(),
                            transaction.getStatus()
                    });
                }
                bottomPanel.setVisible(false); // Hide bottom panel for Transactions
                transactionPanel.setVisible(true); // Show export button for Transactions
                break;

            case "Deposit Request":
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Amount", "Timestamp", "Status"});
                List<AdminDashboard.DepositRequest> depositRequests = dashboard.getDepositRequests();
                for (AdminDashboard.DepositRequest request : depositRequests) {
                    tableModel.addRow(new Object[]{
                            request.getAccountId(),
                            request.getAmount(),
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
                            loan.getAccountId(),
                            loan.getAmount(),
                            loan.getLoanType(),
                            loan.getAppliedDate(),
                            loan.getStatus()
                    });
                }
                accountIdToLabel.setVisible(false);
                accountIdToField.setVisible(false);
                actionButton.setText("Request Loan");
                loanPanel.setVisible(true); // Show export button for Loan Request
                break;

            case "Transfer":
                tableModel.setColumnIdentifiers(new String[]{"Account ID From", "Account ID To", "Amount", "Timestamp", "Status"});
                List<Transfer> transfers = dashboard.getTransfers();
                for (Transfer transfer : transfers) {
                    tableModel.addRow(new Object[]{
                            transfer.getAccountIdFrom(),
                            transfer.getAccountIdTo(),
                            transfer.getAmount(),
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
                            request.getAccountId(),
                            request.getAmount(),
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
                bottomPanel.setVisible(false); // Hide bottom panel for Notifications
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
                    table.addCell(String.valueOf(transaction.getAccountIdFrom()));
                    table.addCell(transaction.getAccountIdTo() == 0 ? "-" : String.valueOf(transaction.getAccountIdTo()));
                    table.addCell(transaction.getType());
                    table.addCell(String.valueOf(transaction.getAmount()));
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
                    table.addCell(String.valueOf(loan.getAccountId()));
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerDashboardUI(new CustomerDashboard(1)).setVisible(true); // Replace 1 with actual customerId
            }
        });
    }
}