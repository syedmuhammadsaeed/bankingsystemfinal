package bankingsystemfinal;

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

public class AdminDashboardUI extends JFrame {
    private bankingsystemfinal.AdminDashboard adminDashboard;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JTable depositTable;
    private JTable loanTable;
    private JTable transferTable;

    public AdminDashboardUI(bankingsystemfinal.AdminDashboard adminDashboard) {
        this.adminDashboard = adminDashboard;
        setTitle("FutureBank - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
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
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(244, 67, 54));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
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

        String[] menuItems = {"Customer Details", "Transaction History", "Deposit Requests", "Loan Requests", "Transfer Requests"};
        for (String menuItem : menuItems) {
            JButton menuButton = new JButton("  " + menuItem);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
            menuButton.setForeground(Color.WHITE);
            menuButton.setBackground(new Color(33, 33, 33));
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setFocusPainted(false);
            menuButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { menuButton.setBackground(new Color(66, 66, 66)); }
                @Override
                public void mouseExited(MouseEvent e) { menuButton.setBackground(new Color(33, 33, 33)); }
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
        String[] customerColumns = {"Customer ID", "Name", "Email", "Phone"};
        DefaultTableModel customerModel = new DefaultTableModel(customerColumns, 0);
        JTable customerTable = new JTable(customerModel);
        customerTable.setBackground(Color.WHITE);
        customerTable.setForeground(Color.BLACK);
        customerTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane customerScroll = new JScrollPane(customerTable);
        customerPanel.add(customerScroll, BorderLayout.CENTER);
        updateCustomerTable(customerTable);
        contentPanel.add(customerPanel, "Customer Details");

        // Transaction History Panel
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBackground(new Color(245, 245, 245));
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] transactionColumns = {"Account ID", "Type", "Amount", "Timestamp"};
        DefaultTableModel transactionModel = new DefaultTableModel(transactionColumns, 0);
        JTable transactionTable = new JTable(transactionModel);
        transactionTable.setBackground(Color.WHITE);
        transactionTable.setForeground(Color.BLACK);
        transactionTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane transactionScroll = new JScrollPane(transactionTable);
        transactionPanel.add(transactionScroll, BorderLayout.CENTER);
        updateTransactionTable(transactionTable);
        contentPanel.add(transactionPanel, "Transaction History");

        // Deposit Requests Panel
        JPanel depositPanel = new JPanel(new BorderLayout());
        depositPanel.setBackground(new Color(245, 245, 245));
        depositPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] depositColumns = {"Account ID", "Amount", "Timestamp", "Status", "Action"};
        DefaultTableModel depositModel = new DefaultTableModel(depositColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 4; }
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
            public boolean isCellEditable(int row, int column) { return column == 5; }
        };
        loanTable = new JTable(loanModel);
        loanTable.setBackground(Color.WHITE);
        loanTable.setForeground(Color.BLACK);
        loanTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        loanTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        loanTable.getColumn("Action").setCellEditor(new LoanButtonEditor(loanTable));
        JScrollPane loanScroll = new JScrollPane(loanTable);
        loanPanel.add(loanScroll, BorderLayout.CENTER);
        updateLoanTable(loanTable);
        contentPanel.add(loanPanel, "Loan Requests");

        // Transfer Requests Panel
        JPanel transferPanel = new JPanel(new BorderLayout());
        transferPanel.setBackground(new Color(245, 245, 245));
        transferPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] transferColumns = {"Account ID From", "Account ID To", "Amount", "Timestamp", "Status", "Action"};
        DefaultTableModel transferModel = new DefaultTableModel(transferColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 5; }
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

        // Add components to frame
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
        for (bankingsystemfinal.TransactionHistory.TransactionRecord transaction : adminDashboard.getTransactionHistory()) {
            model.addRow(new Object[]{
                    transaction.getAccountId(),
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getTimestamp()
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

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(25, 118, 210));
            setForeground(Color.WHITE);
            return this;
        }
    }

    private class DepositButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private JTable table;
        private int adminId = 1; // Placeholder admin ID

        public DepositButtonEditor(JTable table) {
            this.table = table;
            button = new JButton("Action");
            button.setOpaque(true);
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                // Use the selected row instead of editing row
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    // Convert the view row to model row in case of sorting/filtering
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountId = (int) table.getModel().getValueAt(modelRow, 0);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 1).toString());
                    System.out.println("Action button clicked for accountId: " + accountId + ", amount: " + amount);
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) { // Approve
                        System.out.println("Attempting to approve deposit...");
                        if (adminDashboard.approveDeposit(accountId, amount, adminId)) {
                            System.out.println("Deposit approved successfully.");
                            JOptionPane.showMessageDialog(button, "Deposit approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateDepositTable(table);
                        } else {
                            System.out.println("Failed to approve deposit.");
                            JOptionPane.showMessageDialog(button, "Failed to approve deposit. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) { // Reject
                        System.out.println("Attempting to reject deposit...");
                        if (adminDashboard.disapproveDeposit(accountId, amount, adminId)) {
                            System.out.println("Deposit rejected successfully.");
                            JOptionPane.showMessageDialog(button, "Deposit rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateDepositTable(table);
                        } else {
                            System.out.println("Failed to reject deposit.");
                            JOptionPane.showMessageDialog(button, "Failed to reject deposit. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    System.out.println("No row selected.");
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
        private JButton button;
        private JTable table;
        private int adminId = 1; // Placeholder admin ID

        public LoanButtonEditor(JTable table) {
            this.table = table;
            button = new JButton("Action");
            button.setOpaque(true);
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountId = (int) table.getModel().getValueAt(modelRow, 0);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 1).toString());
                    System.out.println("Action button clicked for loan - accountId: " + accountId + ", amount: " + amount);
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) { // Approve
                        System.out.println("Attempting to approve loan...");
                        if (adminDashboard.approveLoan(accountId, amount, adminId)) {
                            System.out.println("Loan approved successfully.");
                            JOptionPane.showMessageDialog(button, "Loan approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateLoanTable(table);
                        } else {
                            System.out.println("Failed to approve loan.");
                            JOptionPane.showMessageDialog(button, "Failed to approve loan. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) { // Reject
                        System.out.println("Attempting to reject loan...");
                        if (adminDashboard.rejectLoan(accountId, amount, adminId)) {
                            System.out.println("Loan rejected successfully.");
                            JOptionPane.showMessageDialog(button, "Loan rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateLoanTable(table);
                        } else {
                            System.out.println("Failed to reject loan.");
                            JOptionPane.showMessageDialog(button, "Failed to reject loan. Check if the request is pending and matches the account and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    System.out.println("No row selected for loan action.");
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
        private JButton button;
        private JTable table;
        private int adminId = 1; // Placeholder admin ID

        public TransferButtonEditor(JTable table) {
            this.table = table;
            button = new JButton("Action");
            button.setOpaque(true);
            button.setBackground(new Color(25, 118, 210));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int accountIdFrom = (int) table.getModel().getValueAt(modelRow, 0);
                    int accountIdTo = (int) table.getModel().getValueAt(modelRow, 1);
                    double amount = Double.parseDouble(table.getModel().getValueAt(modelRow, 2).toString());
                    System.out.println("Action button clicked for transfer - accountIdFrom: " + accountIdFrom + ", accountIdTo: " + accountIdTo + ", amount: " + amount);
                    Object[] options = {"Approve", "Reject"};
                    int choice = JOptionPane.showOptionDialog(button, "Choose action:", "Action",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    if (choice == 0) { // Approve
                        System.out.println("Attempting to approve transfer...");
                        if (adminDashboard.approveTransfer(accountIdFrom, accountIdTo, amount, adminId)) {
                            System.out.println("Transfer approved successfully.");
                            JOptionPane.showMessageDialog(button, "Transfer approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateTransferTable(table);
                        } else {
                            System.out.println("Failed to approve transfer.");
                            JOptionPane.showMessageDialog(button, "Failed to approve transfer. Check if the request is pending and matches the accounts and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (choice == 1) { // Reject
                        System.out.println("Attempting to reject transfer...");
                        if (adminDashboard.rejectTransfer(accountIdFrom, accountIdTo, amount, adminId)) {
                            System.out.println("Transfer rejected successfully.");
                            JOptionPane.showMessageDialog(button, "Transfer rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateTransferTable(table);
                        } else {
                            System.out.println("Failed to reject transfer.");
                            JOptionPane.showMessageDialog(button, "Failed to reject transfer. Check if the request is pending and matches the accounts and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    System.out.println("No row selected for transfer action.");
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
            bankingsystemfinal.AdminDashboard adminDashboard = new bankingsystemfinal.AdminDashboard();
            new AdminDashboardUI(adminDashboard).setVisible(true);
        });
    }
}