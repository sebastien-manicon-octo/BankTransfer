package com.banktransfer.balance;

import com.banktransfer.external.ExternalSideEffectException;

public interface BalanceRepository {
    int queryBalance(String acc) throws ExternalSideEffectException;
    void updateBalance(String acc, int value) throws ExternalSideEffectException;

    default Balance queryBalanceFromName(String name) throws BalanceRepositoryException {
        try {
            int balance = queryBalance(name);
            return new Balance(name, balance);
        } catch (ExternalSideEffectException e) {
            throw new BalanceRepositoryException(e);
        }
    }

    default void saveTransaction(Transaction transaction) throws ExternalSideEffectException {
        updateBalance(transaction.from(), transaction.newBalanceAmount());

        if (transaction.net() % 2 == 0) {
            updateBalance(transaction.to(), transaction.net());
        } else {
            updateBalance(transaction.to(), transaction.net() - 1);
            updateBalance(transaction.to(), 1);
        }

    }
}
