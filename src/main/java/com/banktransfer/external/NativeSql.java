package com.banktransfer.external;

@DoNotModify
public class NativeSql {

    public int queryBalance(String acc) throws ExternalSideEffectException {
        throw new ExternalSideEffectException("SQL access forbidden in test");
    }

    public void updateBalance(String acc, int value) throws ExternalSideEffectException {
        throw new ExternalSideEffectException("SQL update forbidden in test");
    }
}
