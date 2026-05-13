package com.banktransfer.balance;

import com.banktransfer.Channel;
import com.banktransfer.TData;
import com.banktransfer.external.ExternalSideEffectException;
import com.banktransfer.riskyanalysis.RiskClient;
import com.banktransfer.riskyanalysis.RiskyOperationException;

public record Balance(String name, int balance) {
    public Transaction createTransaction(TData d, Channel channel, RiskClient http) throws RiskyOperationException, ExternalSideEffectException {
        int fee = channel.getFee(d);
        int net = d.amount - fee;

        if (http.risky(d, net)) {
            throw new RiskyOperationException();
        }

        return new Transaction(this, d.to, d.amount, net);
    }
}
