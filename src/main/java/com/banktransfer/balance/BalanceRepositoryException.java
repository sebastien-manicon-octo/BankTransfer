package com.banktransfer.balance;

import com.banktransfer.external.ExternalSideEffectException;

public class BalanceRepositoryException extends Exception {
    private final ExternalSideEffectException externalSideEffectException;

    public BalanceRepositoryException(ExternalSideEffectException e) {
        this.externalSideEffectException = e;
    }

    public ExternalSideEffectException getExternalSideEffectException() {
        return externalSideEffectException;
    }
}
