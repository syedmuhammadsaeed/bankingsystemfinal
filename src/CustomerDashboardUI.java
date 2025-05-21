package bankingsystemfinal;

import bankingsystemfinal.Account;
import bankingsystemfinal.AdminDashboard;
import bankingsystemfinal.CustomerDashboard;
import bankingsystemfinal.Loan;
import bankingsystemfinal.TransactionHistory;
import bankingsystemfinal.Transfer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
                // Make all cells non-editable for all tabs
                return false;
            }
        };
        accountsTable = new JTable(tableModel);
        accountsTable.setBackground(Color.WHITE);
        accountsTable.setForeground(Color.BLACK);
        accountsTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Dynamic Bottom Panel
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
                tableModel.setColumnIdentifiers(new String[]{"Account ID", "Type", "Amount", "Timestamp"});
                List<TransactionHistory.TransactionRecord> transactions = dashboard.getTransactions();
                for (TransactionHistory.TransactionRecord transaction : transactions) {
                    tableModel.addRow(new Object[]{
                            transaction.getAccountId(),
                            transaction.getType(),
                            transaction.getAmount(),
                            transaction.getTimestamp()
                    });
                }
                bottomPanel.setVisible(false); // Hide bottom panel for Transactions
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerDashboardUI(new CustomerDashboard(1)).setVisible(true); // Replace 1 with actual customerId
            }
        });
    }
}