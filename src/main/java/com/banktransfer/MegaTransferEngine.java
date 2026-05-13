package com.banktransfer;

import com.banktransfer.balance.BalanceRepository;
import com.banktransfer.balance.CacheBalanceRepository;
import com.banktransfer.balance.SqlBalanceRepository;
import com.banktransfer.external.ExternalSideEffectException;
import com.banktransfer.riskyanalysis.HttpRiskClientWrapper;
import com.banktransfer.riskyanalysis.RiskClient;
import com.banktransfer.riskyanalysis.RiskyOperationException;
import com.banktransfer.state.GState;
import com.banktransfer.state.GStateWrapper;

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
        try {
            if (gState.isInMaintenance()) {
                throw new IllegalStateException("Maintenance");
            }

            gState.incrementTransferCount();

            makeTransaction(d, channel);
            if (gState.getTransferCount() > 100) {
                gState.cacheClear();
            }

            return true;
        } catch (RiskyOperationException e) {
            return false;
        }
    }

    public void makeTransaction(TData d, Channel channel) throws ExternalSideEffectException, RiskyOperationException {
        int fee = channel.getFee(d);
        int net = d.amount - fee;

        // BUG volontaire : net peut être négatif mais on continue

        int balance = balanceRepository.queryBalance(d.from);

        if (http.risky(d, net)) {
            throw new RiskyOperationException();
        }

        balanceRepository.updateBalance(d.from, balance - d.amount);

        if (net % 2 == 0) {
            balanceRepository.updateBalance(d.to, net);
        } else {
            balanceRepository.updateBalance(d.to, net - 1);
            balanceRepository.updateBalance(d.to, 1);
        }
    }
}
