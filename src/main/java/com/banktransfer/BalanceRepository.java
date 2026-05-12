package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;

public interface BalanceRepository {
    int queryBalance(String acc) throws ExternalSideEffectException;
    void updateBalance(String acc, int value) throws ExternalSideEffectException;
}
