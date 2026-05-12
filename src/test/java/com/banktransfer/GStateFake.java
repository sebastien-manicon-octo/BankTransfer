package com.banktransfer;

import java.util.Map;

public class GStateFake implements GState {

    private final boolean maintenance;
    private int transferCount;
    private final Map<String, Integer> cache;

    public GStateFake(boolean maintenance, int transferCount, Map<String, Integer> cache) {
        this.maintenance = maintenance;
        this.transferCount = transferCount;
        this.cache = cache;
    }

    @Override
    public boolean isInMaintenance() {
        return maintenance;
    }

    @Override
    public void incrementTransferCount() {
        transferCount++;
    }

    @Override
    public int getTransferCount() {
        return transferCount;
    }

    @Override
    public boolean cacheContainKey(String key) {
        return cache.containsKey(key);
    }

    @Override
    public int cacheGetValue(String key) {
        return cache.get(key);
    }

    @Override
    public void cachePutValue(String key, int value) {
        cache.put(key, value);
    }

    @Override
    public void cacheClear() {
        cache.clear();
    }
}
