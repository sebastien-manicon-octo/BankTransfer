package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;

public interface RiskClient {
    boolean risky(String acc, int amount) throws ExternalSideEffectException;
}
