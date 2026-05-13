package com.banktransfer;

import com.banktransfer.external.ExternalSideEffectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MegaTransferEngineTest {
    private TData d;
    private BalanceRepository balanceRepository;
    private RiskClient riskClient_true = (c, b) -> true;
    private RiskClient riskClient_false = (c, b) -> false;

    @BeforeEach
    void setUp() throws ExternalSideEffectException {
        d = new TData();
        d.from = "id_user1";
        d.to = "id_user2";
        d.amount = 100;
        d.vip = false;

        balanceRepository = mock(BalanceRepository.class);
        when(balanceRepository.queryBalance(eq("id_user1"))).thenReturn(200);
    }

    @Test
    public void should_do_nothing_when_the_system_is_under_maintenance() {
        GState gState = new GStateFake(true, 0, new HashMap<>());

        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);
        assertThatThrownBy(() ->
                megaTransferEngine.doIt(d, Channel.Other))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void should_charge_mobile_fees_when_channel_is_mobile() throws ExternalSideEffectException {
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Mobile);

        assertThat(result).isTrue();
        verify(balanceRepository).updateBalance("id_user1", 100);
        verify(balanceRepository).updateBalance("id_user2", 98);
    }


    @Test
    public void should_charge_web_fees_when_channel_is_web() throws ExternalSideEffectException {
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Web);

        assertThat(result).isTrue();
        verify(balanceRepository).updateBalance("id_user1", 100);
        verify(balanceRepository).updateBalance("id_user2", 98);
        verify(balanceRepository).updateBalance("id_user2", 1);
    }

    @Test
    public void should_not_charge_fee_when_channel_is_neither_mobile_nor_web() throws ExternalSideEffectException {
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Other);

        assertThat(result).isTrue();
        verify(balanceRepository).updateBalance("id_user1", 100);
        verify(balanceRepository).updateBalance("id_user2", 100);
    }

    @Test
    public void should_reduce_fees_when_transfer_is_vip() throws ExternalSideEffectException {
        d.vip = true;
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Web);

        assertThat(result).isTrue();
        verify(balanceRepository).updateBalance("id_user1", 100);
        verify(balanceRepository).updateBalance("id_user2", 100);
    }

    @Test
    public void should_process_transfer_even_if_risk_alert_is_lifted_but_transfer_is_VIP() throws ExternalSideEffectException {
        d.vip = true;
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_true, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Web);

        assertThat(result).isTrue();
    }

    @Test
    public void should_not_process_transfer_if_risk_alert_is_lifted_and_transfer_is_not_VIP() throws ExternalSideEffectException {
        d.vip = false;
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_true, gState);

        boolean result = megaTransferEngine.doIt(d, Channel.Web);

        assertThat(result).isFalse();
    }

    @Test
    public void should_load_from_balance_if_it_does_not_exist_in_cache() throws ExternalSideEffectException {
        GState gState = new GStateFake(false, 0, new HashMap<>());
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        megaTransferEngine.doIt(d, Channel.Web);

        verify(balanceRepository).queryBalance(eq("id_user1"));
    }

    @Test
    public void should_not_load_from_balance_if_it_exists_in_cache() throws ExternalSideEffectException {
        Map<String, Integer> cache = Map.of("id_user1", 200);
        GState gState = new GStateFake(false, 0, new HashMap<>(cache));
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        megaTransferEngine.doIt(d, Channel.Web);
        verify(balanceRepository, times(0)).queryBalance(eq("id_user1"));
    }

    @Test
    public void should_clear_cache_when_more_than_100_transfer() throws ExternalSideEffectException {
        HashMap<String, Integer> cache = new HashMap<>(Map.of("id_user1", 200));
        GState gState = new GStateFake(false, 101, cache);
        MegaTransferEngine megaTransferEngine = new MegaTransferEngine(balanceRepository, riskClient_false, gState);

        megaTransferEngine.doIt(d, Channel.Web);
        assertThat(cache).isEmpty();
    }
}