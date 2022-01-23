package com.vladd11.app.openstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chest {
    public final int positionX;
    public final int positionY;
    public final int positionZ;
    public final String id;
    public final Side side;
    private final List<Integer> freeSpaceSlots;
    private List<Item> items;

    public Chest(int positionX, int positionY, int positionZ, String id, Side side) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.id = id;
        this.side = side;
        this.freeSpaceSlots = new ArrayList<>();
        this.items = new ArrayList<>();
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

    public List<Integer> getFreeSpaceSlots() {
        return freeSpaceSlots;
    }

    public boolean addFreeSpaceSlot(int slot) {
        return freeSpaceSlots.add(slot);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }
}
