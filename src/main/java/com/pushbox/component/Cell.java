package com.pushbox.component;

public class Cell {
    static final byte NONE = 0;
    static final byte UP1 = 1;
    static final byte UP2 = 2;
    static final byte RIGHT1 = 3;
    static final byte RIGHT2 = 4;
    static final byte DOWN1 = 5;
    static final byte DOWN2 = 6;
    static final byte LEFT1 = 7;
    static final byte LEFT2 = 8;

    boolean black;
    boolean wall;
    boolean floor;
    boolean box;
    boolean ball;
    boolean spirit;
    int action;

    public Cell() {
        black = false;
        wall = false;
        floor = false;
        box = false;
        ball = false;
        spirit = false;
        action = NONE;
    }
}
