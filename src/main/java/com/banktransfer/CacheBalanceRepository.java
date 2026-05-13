package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;

public class CacheBalanceRepository implements BalanceRepository {

    private final BalanceRepository balanceRepository;
    private final GState state;

    public CacheBalanceRepository(BalanceRepository balanceRepository, GState state) {
        this.balanceRepository = balanceRepository;
        this.state = state;
    }

    @Override
    public int queryBalance(String acc) throws ExternalSideEffectException {
        if (state.cacheContainKey(acc)) {
            return state.cacheGetValue(acc);
        }

        int balance = balanceRepository.queryBalance(acc);
        state.cachePutValue(acc, balance);
        return balance;
    }

    @Override
    public void updateBalance(String acc, int value) throws ExternalSideEffectException {
        balanceRepository.updateBalance(acc, value);
    }
}
