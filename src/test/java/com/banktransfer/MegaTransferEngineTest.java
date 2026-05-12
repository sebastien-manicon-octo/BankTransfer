package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MegaTransferEngineTest {

    @Test
    public void test() throws ExternalSideEffectException {
        BalanceRepository repository = mock(BalanceRepository.class);
        when(repository.queryBalance(anyString())).thenReturn(0);

        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(repository);
        TData d = new TData();
        megaTransferEngine.doIt(d, "");
    }
}