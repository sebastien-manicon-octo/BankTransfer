package com.banktransfer.state;

public interface GState {
    boolean isInMaintenance();
    void incrementTransferCount();
    int getTransferCount();
    boolean cacheContainKey(String key);
    int cacheGetValue(String key);
    void cachePutValue(String key, int value);
    void cacheClear();
}
