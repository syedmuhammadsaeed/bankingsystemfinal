package bankingsystemfinal;

import bankingsystemfinal.AdminDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.AbstractCellEditor;
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

public class AdminDashboardUI extends JFrame {
    private bankingsystemfinal.AdminDashboard adminDashboard;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable depositTable;
    private JTable loanTable;
    private JTable transferTable;
    private JTable withdrawTable;
    private JTable transactionTable;
    private final String[] customerColumns = {"Customer ID", "Name", "Email", "Phone"};

    public AdminDashboardUI(bankingsystemfinal.AdminDashboard adminDashboard) {
        this.adminDashboard = adminDashboard;
        setTitle("FutureBank - Admin Dashboard");
        setSize(1200, 800);
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 188, 156));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        JLabel headerLabel = new JLabel("FutureBank Admin");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Admin | ");
        userLabel.setForeground(Color.WHITE);
        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.addActionListener(e -> {
            dispose();
            new bankingsystemfinal.LoginUI(new bankingsystemfinal.Admin()).setVisible(true);
        });
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(33, 33, 33));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"Customer Details", "Transaction History", "Deposit Requests", "Loan Requests", "Transfer Requests", "Withdraw Requests", "Notifications"};
        Color[] buttonColors = {
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156),
                new Color(26, 188, 156)
        };

        for (int i = 0; i < menuItems.length; i++) {
            RoundedButton menuButton = new RoundedButton("  " + menuItems[i]);
            final Color originalColor = buttonColors[i];
            final String menuItem = menuItems[i];
            menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
            menuButton.setForeground(Color.WHITE);
            menuButton.setBackground(originalColor);
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            menuButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuButton.setBackground(menuButton.getBackground().brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    menuButton.setBackground(originalColor);
                }
            });
            menuButton.addActionListener(e -> cardLayout.show(contentPanel, menuItem));
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }

        // Main Content
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(new Color(245, 245, 245));

        // Customer Details Panel
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(new Color(245, 245, 245));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        DefaultTableModel customerModel = new DefaultTableModel(customerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable customerTable = new JTable(customerModel);
        customerTable.setBackground(Color.WHITE);
        customerTable.setForeground(Color.BLACK);
        customerTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane customerScroll = new JScrollPane(customerTable);
        customerPanel.add(customerScroll, BorderLayout.CENTER);

        // Add Delete Account Button
        RoundedButton deleteButton = new RoundedButton("Delete Account");
        deleteButton.setBackground(new Color(220, 20, 60)); // Red for deletion
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customerTable.getModel().getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(customerPanel,
                        "Are you sure you want to delete this customer and their account? This action cannot be undone.",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (adminDashboard.deleteCustomerAccount(customerId, 1)) { // Assuming adminId = 1
                        JOptionPane.showMessageDialog(customerPanel, "Customer and account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateCustomerTable(customerTable);
                    } else {
                        JOptionPane.showMessageDialog(customerPanel, "Failed to delete customer and account.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(customerPanel, "Please select a customer to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        customerPanel.add(deleteButton, BorderLayout.SOUTH);

        updateCustomerTable(customerTable);
        contentPanel.add(customerPanel, "Customer Details");

        // Transaction History Panel
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBackground(new Color(245, 245, 245));
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] transactionColumns = {"Account ID From", "Account ID To", "Type", "Amount", "Timestamp", "Status"};
        DefaultTableModel transactionModel = new DefaultTableModel(transactionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionModel);
        transactionTable.setBackground(Color.WHITE);
        transactionTable.setForeground(Color.BLACK);
        transactionTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane transactionScroll = new JScrollPane(transactionTable);
        transactionPanel.add(transactionScroll, BorderLayout.CENTER);

        RoundedButton exportTransactionButton = new RoundedButton("Export to PDF");
        exportTransactionButton.setBackground(new Color(25, 118, 210));
        exportTransactionButton.setForeground(Color.WHITE);
        exportTransactionButton.setPreferredSize(new Dimension(120, 30));
        exportTransactionButton.addActionListener(e -> exportTransactionHistoryToPDF());
        transactionPanel.add(exportTransactionButton, BorderLayout.SOUTH);

        updateTransactionTable(transactionTable);
        contentPanel.add(transactionPanel, "Transaction History");

        // Deposit Requests Panel
        JPanel depositPanel = new JPanel(new BorderLayout());
        depositPanel.setBackground(new Color(245, 245, 245));
        depositPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] depositColumns = {"Account ID", "Amount", "Timestamp", "Status", "Action"};
        DefaultTableModel depositModel = new DefaultTableModel(depositColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        depositTable = new JTable(depositModel);
        depositTable.setBackground(Color.WHITE);
        depositTable.setForeground(Color.BLACK);
        depositTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        depositTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        depositTable.getColumn("Action").setCellEditor(new DepositButtonEditor(depositTable));
        JScrollPane depositScroll = new JScrollPane(depositTable);
        depositPanel.add(depositScroll, BorderLayout.CENTER);
        updateDepositTable(depositTable);
        contentPanel.add(depositPanel, "Deposit Requests");

        // Loan Requests Panel
        JPanel loanPanel = new JPanel(new BorderLayout());
        loanPanel.setBackground(new Color(245, 245, 245));
        loanPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] loanColumns = {"Account ID", "Amount", "Loan Type", "Applied Date", "Status", "Action"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        loanTable = new JTable(loanModel);
        loanTable.setBackground(Color.WHITE);
        loanTable.setForeground(Color.BLACK);
        loanTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        loanTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        loanTable.getColumn("Action").setCellEditor(new LoanButtonEditor(loanTable));
        JScrollPane loanScroll = new JScrollPane(loanTable);
        loanPanel.add(loanScroll, BorderLayout.CENTER);

        RoundedButton exportLoanButton = new RoundedButton("Export to PDF");
        exportLoanButton.setBackground(new Color(25, 118, 210));
        exportLoanButton.setForeground(Color.WHITE);
        exportLoanButton.setPreferredSize(new Dimension(120, 30));
        exportLoanButton.addActionListener(e -> exportLoanRequestsToPDF());
        loanPanel.add(exportLoanButton, BorderLayout.SOUTH);

        updateLoanTable(loanTable);
        contentPanel.add(loanPanel, "Loan Requests");

        // Transfer Requests Panel
        JPanel transferPanel = new JPanel(new BorderLayout());
        transferPanel.setBackground(new Color(245, 245, 245));
        transferPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] transferColumns = {"Account ID From", "Account ID To", "Amount", "Timestamp", "Status", "Action"};
        DefaultTableModel transferModel = new DefaultTableModel(transferColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        transferTable = new JTable(transferModel);
        transferTable.setBackground(Color.WHITE);
        transferTable.setForeground(Color.BLACK);
        transferTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        transferTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        transferTable.getColumn("Action").setCellEditor(new TransferButtonEditor(transferTable));
        JScrollPane transferScroll = new JScrollPane(transferTable);
        transferPanel.add(transferScroll, BorderLayout.CENTER);
        updateTransferTable(transferTable);
        contentPanel.add(transferPanel, "Transfer Requests");

        // Withdraw Requests Panel
        JPanel withdrawPanel = new JPanel(new BorderLayout());
        withdrawPanel.setBackground(new Color(245, 245, 245));
        withdrawPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] withdrawColumns = {"Account ID", "Amount", "Timestamp", "Status", "Action"};
        DefaultTableModel withdrawModel = new DefaultTableModel(withdrawColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        withdrawTable = new JTable(withdrawModel);
        withdrawTable.setBackground(Color.WHITE);
        withdrawTable.setForeground(Color.BLACK);
        withdrawTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        withdrawTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        withdrawTable.getColumn("Action").setCellEditor(new WithdrawButtonEditor(withdrawTable));
        JScrollPane withdrawScroll = new JScrollPane(withdrawTable);
        withdrawPanel.add(withdrawScroll, BorderLayout.CENTER);
        updateWithdrawTable(withdrawTable);
        contentPanel.add(withdrawPanel, "Withdraw Requests");

        // Notifications Panel
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        notificationsPanel.setBackground(new Color(245, 245, 245));
        notificationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel customerModelForNotifications = new DefaultTableModel(customerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable customerTableForNotifications = new JTable(customerModelForNotifications);
        customerTableForNotifications.setBackground(Color.WHITE);
        customerTableForNotifications.setForeground(Color.BLACK);
        customerTableForNotifications.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane customerScrollForNotifications = new JScrollPane(customerTableForNotifications);
        updateCustomerTable(customerTableForNotifications);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextArea messageArea = new JTextArea(5, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        RoundedButton sendButton = new RoundedButton("Send Notification");
        sendButton.setBackground(new Color(25, 118, 210));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> {
            int selectedRow = customerTableForNotifications.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customerTableForNotifications.getModel().getValueAt(selectedRow, 0);
                String message = messageArea.getText().trim();
                if (!message.isEmpty()) {
                    if (adminDashboard.sendNotification(customerId, message)) {
                        JOptionPane.showMessageDialog(notificationsPanel, "Notification sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        messageArea.setText("");
                    } else {
                        JOptionPane.showMessageDialog(notificationsPanel, "Failed to send notification.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(notificationsPanel, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(notificationsPanel, "Please select a customer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Message:"), BorderLayout.NORTH);
        inputPanel.add(messageScroll, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.SOUTH);

        notificationsPanel.add(customerScrollForNotifications, BorderLayout.CENTER);
        notificationsPanel.add(inputPanel, BorderLayout.SOUTH);
        contentPanel.add(notificationsPanel, "Notifications");

        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void updateCustomerTable(JTable customerTable) {
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.Customer.CustomerRecord customer : adminDashboard.getCustomerDetails()) {
            model.addRow(new Object[]{
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhoneNumber()
            });
        }
    }

    private void updateTransactionTable(JTable transactionTable) {
        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.AdminDashboard.TransactionRecord transaction : adminDashboard.getTransactionHistory()) {
            model.addRow(new Object[]{
                    transaction.getAccountIdFrom(),
                    transaction.getAccountIdTo() == 0 ? "-" : String.valueOf(transaction.getAccountIdTo()),
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getTimestamp(),
                    transaction.getStatus()
            });
        }
    }

    private void updateDepositTable(JTable depositTable) {
        DefaultTableModel model = (DefaultTableModel) depositTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.AdminDashboard.DepositRequest request : adminDashboard.getDepositRequests()) {
            model.addRow(new Object[]{
                    request.getAccountId(),
                    String.format("%.2f", request.getAmount()),
                    request.getTimestamp(),
                    request.getStatus(),
                    "Action"
            });
        }
    }

    private void updateLoanTable(JTable loanTable) {
        DefaultTableModel model = (DefaultTableModel) loanTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.Loan.LoanRecord loan : adminDashboard.getLoanRequests()) {
            model.addRow(new Object[]{
                    loan.getAccountId(),
                    String.format("%.2f", loan.getAmount()),
                    loan.getLoanType(),
                    loan.getAppliedDate(),
                    loan.getStatus(),
                    "Action"
            });
        }
    }

    private void updateTransferTable(JTable transferTable) {
        DefaultTableModel model = (DefaultTableModel) transferTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.Transfer transfer : adminDashboard.getTransferRequests()) {
            model.addRow(new Object[]{
                    transfer.getAccountIdFrom(),
                    transfer.getAccountIdTo(),
                    String.format("%.2f", transfer.getAmount()),
                    transfer.getTimestamp(),
                    transfer.getStatus(),
                    "Action"
            });
        }
    }

    private void updateWithdrawTable(JTable withdrawTable) {
        DefaultTableModel model = (DefaultTableModel) withdrawTable.getModel();
        model.setRowCount(0);
        for (bankingsystemfinal.AdminDashboard.WithdrawRequest request : adminDashboard.getWithdrawRequests()) {
            model.addRow(new Object[]{
                    request.getAccountId(),
                    String.format("%.2f", request.getAmount()),
                    request.getTimestamp(),
                    request.getStatus(),
                    "Action"
            });
        }
    }

    private void exportTransactionHistoryToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Transaction_History_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 PdfWriter writer = new PdfWriter(fos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph("Transaction History"));
                document.add(new Paragraph("Date: " + java.time.LocalDateTime.now()));

                Table table = new Table(6);
                table.addCell("Account ID From");
                table.addCell("Account ID To");
                table.addCell("Type");
                table.addCell("Amount");
                table.addCell("Timestamp");
                table.addCell("Status");

                for (bankingsystemfinal.AdminDashboard.TransactionRecord transaction : adminDashboard.getTransactionHistory()) {
                    table.addCell(String.valueOf(transaction.getAccountIdFrom()));
                    table.addCell(transaction.getAccountIdTo() == 0 ? "-" : String.valueOf(transaction.getAccountIdTo()));
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

    private void exportLoanRequestsToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Loan_Requests_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file);
                 PdfWriter writer = new PdfWriter(fos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph("Loan Requests"));
                document.add(new Paragraph("Date: " + java.time.LocalDateTime.now()));

                Table table = new Table(5);
                table.addCell("Account ID");
                table.addCell("Amount");
                table.addCell("Loan Type");
                table.addCell("Applied Date");
                table.addCell("Status");

                for (bankingsystemfinal.Loan.LoanRecord loan : adminDashboard.getLoanRequests()) {
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

    private class ButtonRenderer extends RoundedButton implements TableCellRenderer {
        public ButtonRenderer() {
            super("Action");
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Action" : value.toString());
            setBackground(new Color(25, 118, 210));
            setForeground(Color.WHITE);
            return this;
        }
    }

    private class DepositButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private RoundedButton button;
        private JTable table;
        private int adminId = 1;

        public DepositButtonEditor(JTable table) {
            this.table = table;
            button = new RoundedButton("Action");
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountId = (int) table.getModel().getValueAt(modelRow, 0);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 1).toString());
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        if (adminDashboard.approveDeposit(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Deposit approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateDepositTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to approve deposit. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) {
                        if (adminDashboard.disapproveDeposit(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Deposit rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateDepositTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to reject deposit. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Please select a row to perform an action.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText("Action");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private class LoanButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private RoundedButton button;
        private JTable table;
        private int adminId = 1;

        public LoanButtonEditor(JTable table) {
            this.table = table;
            button = new RoundedButton("Action");
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountId = (int) table.getModel().getValueAt(modelRow, 0);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 1).toString());
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        if (adminDashboard.approveLoan(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Loan approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateLoanTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to approve loan. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) {
                        if (adminDashboard.rejectLoan(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Loan rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateLoanTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to reject loan. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Please select a row to perform an action.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText("Action");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private class TransferButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private RoundedButton button;
        private JTable table;
        private int adminId = 1;

        public TransferButtonEditor(JTable table) {
            this.table = table;
            button = new RoundedButton("Action");
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountIdFrom = (int) table.getModel().getValueAt(modelRow, 0);
                    int accountIdTo = (int) table.getModel().getValueAt(modelRow, 1);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 2).toString());
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        if (adminDashboard.approveTransfer(accountIdFrom, accountIdTo, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Transfer approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateTransferTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to approve transfer. Check if the request is pending and matches the accounts and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) {
                        if (adminDashboard.rejectTransfer(accountIdFrom, accountIdTo, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Transfer rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateTransferTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to reject transfer. Check if the request is pending and matches the accounts and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Please select a row to perform an action.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText("Action");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private class WithdrawButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private RoundedButton button;
        private JTable table;
        private int adminId = 1;

        public WithdrawButtonEditor(JTable table) {
            this.table = table;
            button = new RoundedButton("Action");
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountId = (int) table.getModel().getValueAt(modelRow, 0);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 1).toString());
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) {
                        if (adminDashboard.approveWithdraw(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Withdraw approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateWithdrawTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to approve withdraw. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) {
                        if (adminDashboard.rejectWithdraw(accountId, amount, adminId)) {
                            JOptionPane.showMessageDialog(button, "Withdraw rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateWithdrawTable(table);
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to reject withdraw. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Please select a row to perform an action.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText("Action");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard();
            AdminDashboardUI adminUI = new AdminDashboardUI(adminDashboard);
            adminUI.setVisible(true);
        });
    }
}