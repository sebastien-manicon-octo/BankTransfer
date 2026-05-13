package com.banktransfer.state;

import com.banktransfer.external.GlobalState;

public class GStateWrapper implements GState {
    @Override
    public boolean isInMaintenance() {
        return GlobalState.maintenance;
    }

    @Override
    public void incrementTransferCount() {
        GlobalState.transferCount++;
    }

    @Override
    public int getTransferCount() {
        return GlobalState.transferCount;
    }

    @Override
    public boolean cacheContainKey(String key) {
        return GlobalState.cache.containsKey(key);
    }

    @Override
    public int cacheGetValue(String key) {
        return GlobalState.cache.get(key);
    }

    @Override
    public void cachePutValue(String key, int value) {
        GlobalState.cache.put(key, value);
    }

    @Override
    public void cacheClear() {
        GlobalState.cache.clear();
    }
}
