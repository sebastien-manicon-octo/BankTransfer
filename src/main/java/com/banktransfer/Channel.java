package com.banktransfer;

public enum Channel {
    Mobile(2), Web(1), Other(0);

    private int fee;

    Channel(int fee) {
        this.fee = fee;
    }

    public int getFee(TData d) {
        return d.vip
                ? fee - 1
                : fee;
    }
}
