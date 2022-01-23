package com.vladd11.app.openstorage;

public enum Side {
    BOTTOM,
    TOP,
    BACK,
    FRONT,
    RIGHT,
    LEFT;

    public boolean isInaccessible() {
        return this == LEFT || this == RIGHT || this == BACK;
    }
}
