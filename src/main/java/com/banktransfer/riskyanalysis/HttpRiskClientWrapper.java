package com.banktransfer.riskyanalysis;

import com.banktransfer.external.ExternalSideEffectException;
import com.banktransfer.external.HttpRiskClient;

public class HttpRiskClientWrapper implements RiskClient {
    HttpRiskClient http = new HttpRiskClient();

    @Override
    public boolean risky(String acc, int amount) throws ExternalSideEffectException {
        return http.risky(acc, amount);
    }
}
