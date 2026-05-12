package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;
import org.junit.jupiter.api.Test;

class MegaTransferEngineTest {

    @Test
    public void test() throws ExternalSideEffectException {
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine();
        TData d = new TData();
        megaTransferEngine.doIt(d, "");
    }
}