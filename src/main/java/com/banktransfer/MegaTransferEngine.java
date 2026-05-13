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
        this.balanceRepository = new CacheBalanceRepository(balanceRepository, gState);
        this.http = riskClient;
        this.gState = gState;
    }

    public boolean doIt(TData d, Channel channel) throws ExternalSideEffectException {

        if (gState.isInMaintenance()) {
            throw new IllegalStateException("Maintenance");
        }

        gState.incrementTransferCount();

        int fee = channel.getFee(d);
        int net = d.amount - fee;

        // BUG volontaire : net peut être négatif mais on continue

        int balance = balanceRepository.queryBalance(d.from);

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
