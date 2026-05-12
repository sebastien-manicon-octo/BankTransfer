package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;

public class MegaTransferEngine {

    private final BalanceRepository balanceRepository;
    private final RiskClient http;
    private final GState gState;

    public MegaTransferEngine() {
        this(new SqlBalanceRepository(), new HttpRiskClientWrapper(), new GStateWrapper());

    }

    public MegaTransferEngine(BalanceRepository balanceRepository, RiskClient riskClient, GState gState) {
        this.balanceRepository = balanceRepository;
        this.http = riskClient;
        this.gState = gState;
    }

    public boolean doIt(TData d, String channel) throws ExternalSideEffectException {

        if (gState.isInMaintenance()) {
            throw new IllegalStateException("Maintenance");
        }

        gState.incrementTransferCount();

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
        if (gState.cacheContainKey(d.a)) {
            balance = gState.cacheGetValue(d.a);
        } else {
            balance = balanceRepository.queryBalance(d.a);
            gState.cachePutValue(d.a, balance);
        }



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

        if (gState.getTransferCount() > 100) {
            gState.cacheClear();
        }

        return true;
    }
}
