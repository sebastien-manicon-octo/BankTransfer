package com.banktransfer.external;

@DoNotModify
public class HttpRiskClient {

    public boolean risky(String acc, int amount) throws ExternalSideEffectException {
        throw new ExternalSideEffectException("HTTP risk service forbidden in test");
    }
}
