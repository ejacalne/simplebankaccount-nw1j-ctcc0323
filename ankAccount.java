package neyney;

import java.util.ArrayList;
import java.util.List;

class ankAccount {
    private final String accountNumber;
    private final String password;
    private double balance;
    private final List<String> transactionHistory;

    public ankAccount(String accountNumber, String password) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: $" + amount);
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactionHistory.add("Withdrew: $" + amount);
            return true;
        } else {
            return false;
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getTransactionHistory() {
        StringBuilder history = new StringBuilder();
        for (String transaction : transactionHistory) {
            history.append(transaction).append("\n");
        }
        return history.toString();
    }
}

