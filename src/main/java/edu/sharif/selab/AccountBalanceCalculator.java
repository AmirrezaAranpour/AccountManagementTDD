package edu.sharif.selab;

import java.util.ArrayList;
import java.util.List;

public class AccountBalanceCalculator {

    private static List<Transaction> transactionHistory = new ArrayList<>();

    // Method to calculate balance based on transactions
    public static int calculateBalance(List<Transaction> transactions) {
        if (transactions == null)
            throw new IllegalArgumentException("Transaction list cannot be null");

        int balance = 0;
        for (Transaction t : transactions) {
            if (t.getAmount() < 0)
                throw new IllegalArgumentException("Transaction amount cannot be negative");

            if (t.getType() == TransactionType.DEPOSIT)
                balance += t.getAmount();
            else // WITHDRAWAL
                balance -= t.getAmount();
        }

        /* ---------- NEW ---------- */
        // storing the last set of transactions
        transactionHistory = new ArrayList<>(transactions);
        /* ------------------------- */

        return balance;
    }

    // Method to get the transaction history
    public static List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory); // Return a copy to prevent external modification
    }

    // Method to add a transaction to the history
    public static void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    // Method to clear the transaction history
    public static void clearTransactionHistory() {
        transactionHistory.clear();
    }
}
