package com.banktransfer;

import com.banktransfer.balance.*;
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
        } catch (BalanceRepositoryException e) {
            throw e.getExternalSideEffectException();
        }
    }

    public void makeTransaction(TData d, Channel channel) throws ExternalSideEffectException, RiskyOperationException, BalanceRepositoryException {
        Balance balance = balanceRepository.queryBalanceFromName(d.from);
        Transaction transaction = balance.createTransaction(d, channel, http);
        balanceRepository.saveTransaction(transaction);
    }
}
