package neyney;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ankApp extends JFrame {
    private JTextArea displayArea;
    private JPanel mainPanel;
    private final JPanel controlPanel;
    private CardLayout cardLayout;
    private HashMap<String, ankAccount> accounts;
    private ankAccount currentAccount;

    public ankApp() {
        accounts = new HashMap<>();
        setTitle("Simple Bank Account Management System");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel loginPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField accountNumberField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("LOGIN");
        loginButton.addActionListener(e -> {
            String accountNumber = accountNumberField.getText();
            String password = new String(passwordField.getPassword());
            ankAccount account = accounts.get(accountNumber);
            if (account != null && account.verifyPassword(password)) {
                currentAccount = account;
                displayArea.setText("Logged in to account: " + accountNumber + "\n");
                cardLayout.show(mainPanel, "ControlPanel");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid account number or password.");
            }
        });

        JButton signupButton = new JButton("Create Account");
        signupButton.addActionListener(e -> {
            String accountNumber = JOptionPane.showInputDialog("Enter account number:");
            if (accountNumber != null && !accountNumber.isEmpty() && !accounts.containsKey(accountNumber)) {
                String password = JOptionPane.showInputDialog("Enter password:");
                accounts.put(accountNumber, new ankAccount(accountNumber, password));
                displayArea.setText("Account created: " + accountNumber + "\n");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid account number or account already exists.");
            }
        });

        loginPanel.add(new JLabel("Account Number"));
        loginPanel.add(accountNumberField);
        loginPanel.add(new JLabel("Password/Pin"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(signupButton);
        
        mainPanel.add(loginPanel, "LoginPanel");

        controlPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new DepositListener());
        controlPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new WithdrawListener());
        controlPanel.add(withdrawButton);

        JButton balanceInquiryButton = new JButton("Balance Inquiry");
        balanceInquiryButton.addActionListener(new BalanceInquiryListener());
        controlPanel.add(balanceInquiryButton);

        JButton transactionHistoryButton = new JButton("Transaction History");
        transactionHistoryButton.addActionListener(new TransactionHistoryListener());
        controlPanel.add(transactionHistoryButton);

        JButton billPaymentsButton = new JButton("Bill Payments");
        billPaymentsButton.addActionListener(new BillPaymentsListener());
        controlPanel.add(billPaymentsButton);

        JButton quickTransferButton = new JButton("Quick Transfer");
        quickTransferButton.addActionListener(new QuickTransferListener());
        controlPanel.add(quickTransferButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            currentAccount = null;
            displayArea.setText("Logged out.\n");
            cardLayout.show(mainPanel, "LoginPanel");
        });
        controlPanel.add(logoutButton);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(scrollPane, BorderLayout.CENTER);
        userPanel.add(controlPanel, BorderLayout.SOUTH);

        mainPanel.add(userPanel, "ControlPanel");
        add(mainPanel);

        cardLayout.show(mainPanel, "LoginPanel");
    }

    private class DepositListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                String amountStr = JOptionPane.showInputDialog("Enter deposit amount:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    currentAccount.deposit(amount);
                    displayArea.append("Deposited $" + amount + "\n");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    private class WithdrawListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                String amountStr = JOptionPane.showInputDialog("Enter withdrawal amount:");
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (currentAccount.withdraw(amount)) {
                        displayArea.append("Withdrew $" + amount + "\n");
                    } else {
                        JOptionPane.showMessageDialog(null, "Insufficient funds.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    private class BalanceInquiryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                double balance = currentAccount.getBalance();
                displayArea.append("Balance: $" + balance + "\n");
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    private class TransactionHistoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                displayArea.append("Transaction history:\n");
                displayArea.append(currentAccount.getTransactionHistory());
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    private class BillPaymentsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                String[] options = {"Electricity Bill", "Water Bill", "Internet Bill"};
                String choice = (String) JOptionPane.showInputDialog(null, "Choose a bill to pay:",
                        "Bill Payments", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (choice != null) {
                    String amountStr = JOptionPane.showInputDialog("Enter payment amount for " + choice + ":");
                    try {
                        double amount = Double.parseDouble(amountStr);
                        if (currentAccount.withdraw(amount)) {
                            displayArea.append("Paid $" + amount + " for " + choice + "\n");
                        } else {
                            JOptionPane.showMessageDialog(null, "Insufficient funds.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid amount.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    private class QuickTransferListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentAccount != null) {
                String targetAccountNumber = JOptionPane.showInputDialog("Enter target account number:");
                ankAccount targetAccount = accounts.get(targetAccountNumber);
                if (targetAccount != null) {
                    String amountStr = JOptionPane.showInputDialog("Enter transfer amount:");
                    try {
                        double amount = Double.parseDouble(amountStr);
                        if (currentAccount.withdraw(amount)) {
                            targetAccount.deposit(amount);
                            displayArea.append("Transferred $" + amount + " to account: " + targetAccountNumber + "\n");
                        } else {
                            JOptionPane.showMessageDialog(null, "Insufficient funds.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid amount.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Target account not found.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please log in first.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ankApp app = new ankApp();
            app.setVisible(true);
        });
    }
}
