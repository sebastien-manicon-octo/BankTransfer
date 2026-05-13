package com.banktransfer.riskyanalysis;

import com.banktransfer.TData;
import com.banktransfer.external.ExternalSideEffectException;

public interface RiskClient {
    boolean risky(String acc, int amount) throws ExternalSideEffectException;

    default boolean risky(TData d, int amountWithFeeApplied) throws ExternalSideEffectException {
        return risky(d.from, amountWithFeeApplied) && !d.vip;
    }
}
