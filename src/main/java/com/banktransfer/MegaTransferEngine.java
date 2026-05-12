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

        if (d.vip) {
            fee--;
        }

        int net = d.amount - fee;

        // BUG volontaire : net peut être négatif mais on continue

        int balance;
        if (gState.cacheContainKey(d.from)) {
            balance = gState.cacheGetValue(d.from);
        } else {
            balance = balanceRepository.queryBalance(d.from);
            gState.cachePutValue(d.from, balance);
        }



        if (http.risky(d.from, net)) {
            if (!d.vip) {
                return false;
            }
        }

        balanceRepository.updateBalance(d.from, balance - d.amount);

        if (net % 2 == 0) {
            balanceRepository.updateBalance(d.to, net);
        } else {
            balanceRepository.updateBalance(d.to, net - 1);
            balanceRepository.updateBalance(d.to, 1);
        }

        if (gState.getTransferCount() > 100) {
            gState.cacheClear();
        }

        return true;
    }
}
