package com.banktransfer.balance;

public record Transaction(Balance balance, String to, int amount, int net) {
    public String from() {
        return balance().name();
    }

    public int newBalanceAmount() {
        return balance.balance() - amount;
    }
}
