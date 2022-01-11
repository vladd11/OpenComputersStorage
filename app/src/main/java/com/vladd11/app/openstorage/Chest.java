package com.vladd11.app.openstorage;

public class Chest {
    public final int positionX;
    public final int positionY;
    public final int positionZ;
    public final String id;
    public final int side;

    public Chest(int positionX, int positionY, int positionZ, String id, int side) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.id = id;
        this.side = side;
    }
}
