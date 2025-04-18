package edu.sharif.selab;

import java.util.ArrayList;
import java.util.List;

public class AccountBalanceCalculator {

    private static List<Transaction> transactionHistory = new ArrayList<>();

    // Method to calculate balance based on transactions
    public static int calculateBalance(List<Transaction> transactions) {
        int balance = 0;
        transactionHistory.clear();        // پاک‌سازی تاریخچهٔ قبلی

        for (Transaction t : transactions) {
            switch (t.getType()) {
                case DEPOSIT    -> balance += t.getAmount();
                case WITHDRAWAL -> balance -= t.getAmount();
            }
            transactionHistory.add(t);     // ثبت تراکنش جاری
        }
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
