package com.ncode.async;

public enum EventType {
    LIKE(1),
    MAIL(2),
    LOGIN(3),
    COMMENT(4),
    FOLLOW(5);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
