package com.jamesanton;

public enum StatType {
    CONFIRMED("Confirmed"),
    DEATHS ("Deaths"),
    RECOVERED ("Recovered");

    private String statUrlSegment;

    StatType(String statUrlSegment) {
        this.statUrlSegment = statUrlSegment;
    }

    public String getUrlSegment() {
        return statUrlSegment;
    }
}
