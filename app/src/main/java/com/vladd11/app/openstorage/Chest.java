package com.vladd11.app.openstorage;

import java.util.Objects;

public class Chest {
    public final int positionX;
    public final int positionY;
    public final int positionZ;
    public final String id;
    public final Side side;
    private int freeSpace;

    public Chest(int positionX, int positionY, int positionZ, String id, Side side) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.id = id;
        this.side = side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chest chest = (Chest) o;
        return positionX == chest.positionX && positionY == chest.positionY && positionZ == chest.positionZ && id.equals(chest.id) && side == chest.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionX, positionY, positionZ, id, side);
    }

    public int getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(int freeSpace) {
        if(freeSpace < 0) throw new IllegalArgumentException("freeSpace must be >= 0");
        this.freeSpace = freeSpace;
    }

    public int incrementFreeSpace() {
        return freeSpace++;
    }
}
