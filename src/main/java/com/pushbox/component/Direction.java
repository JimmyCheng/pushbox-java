package com.pushbox.component;

public enum Direction {
    UP(0), RIGHT(1), DOWN(2), LEFT(3);

    int order;

    Direction(int id) {
        order = id;
    }

    public int getOrder() {
        return order;
    }
}
