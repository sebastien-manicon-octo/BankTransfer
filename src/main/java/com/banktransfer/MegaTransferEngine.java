package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;
import com.banktransfer.external.GlobalState;
import com.banktransfer.external.HttpRiskClient;

public class MegaTransferEngine {

    private BalanceRepository balanceRepository;

    public MegaTransferEngine() {
        this(new SqlBalanceRepository());

    }

    public MegaTransferEngine(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public boolean doIt(TData d, String channel) throws ExternalSideEffectException {

        if (GlobalState.maintenance) {
            throw new IllegalStateException("Maintenance");
        }

        GlobalState.transferCount++;

        int fee = 0;

        if ("MOBILE".equals(channel)) {
            fee += 2;
        } else {
            if ("WEB".equals(channel)) {
                fee += 1;
            }
        }

        if (d.d) {
            fee--;
        }

        int net = d.c - fee;

        // BUG volontaire : net peut être négatif mais on continue

        int balance;
        if (GlobalState.cache.containsKey(d.a)) {
            balance = GlobalState.cache.get(d.a);
        } else {
            balance = balanceRepository.queryBalance(d.a);
            GlobalState.cache.put(d.a, balance);
        }

        HttpRiskClient http = new HttpRiskClient();

        if (http.risky(d.a, net)) {
            if (!d.d) {
                return false;
            }
        }

        balanceRepository.updateBalance(d.a, balance - d.c);

        if (net % 2 == 0) {
            balanceRepository.updateBalance(d.b, net);
        } else {
            balanceRepository.updateBalance(d.b, net - 1);
            balanceRepository.updateBalance(d.b, 1);
        }

        if (GlobalState.transferCount > 100) {
            GlobalState.cache.clear();
        }

        return true;
    }
}
