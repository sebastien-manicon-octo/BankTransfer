package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MegaTransferEngineTest {

    @Test
    public void test() throws ExternalSideEffectException {
        BalanceRepository repository = mock(BalanceRepository.class);
        when(repository.queryBalance(anyString())).thenReturn(0);

        RiskClient riskClient = mock(RiskClient.class);
        when(riskClient.risky(anyString(), anyInt())).thenReturn(true);

        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(repository, riskClient);
        TData d = new TData();
        megaTransferEngine.doIt(d, "");
    }
}