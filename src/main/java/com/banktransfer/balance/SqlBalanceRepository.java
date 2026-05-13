package com.banktransfer.balance;

import com.banktransfer.external.ExternalSideEffectException;
import com.banktransfer.external.NativeSql;

public class SqlBalanceRepository implements BalanceRepository {
    NativeSql sql = new NativeSql();

    @Override
    public int queryBalance(String acc) throws ExternalSideEffectException {
        return sql.queryBalance(acc);
    }

    @Override
    public void updateBalance(String acc, int value) throws ExternalSideEffectException {
        sql.updateBalance(acc, value);
    }
}
