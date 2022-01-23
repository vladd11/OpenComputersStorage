package com.vladd11.app.openstorage;

import androidx.annotation.NonNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramGenerator {
    public ProgramGenerator() {
        builder = new StringBuilder();
        builder.append("local component = require(\"component\")\nlocal robot = require(\"robot\")\nlocal movement, utils, update = ...\n");
    }

    private final StringBuilder builder;

    public void listItems() {
        builder.append("utils.list()\n");
    }

    @SuppressWarnings("ComparatorCombinators") // items.sort requires Android N
    public void takeItems(List<Item> items) {
        Collections.sort(items, (o1, o2) -> o1.chest.positionX - o2.chest.positionX);
        for (int i = 0; i < items.size(); i++) { // I use it because in this case it will be much more readable than foreach
            final Item item = items.get(i);
            Side side = item.chest.side;
            if (i == 0 || !items.get(i - 1).chest.equals(item.chest)) {
                goTo(item.chest.positionX, item.chest.positionY, item.chest.positionZ);
                side = turnToSide(item.chest.side);
            } else if (side.isInaccessible()) {
                side = Side.FRONT;
            }

            builder.append("component.inventory_controller.suckFromSlot(");
            builder.append(side.ordinal());
            builder.append(',');
            builder.append(item.position);
            builder.append(',');
            builder.append(item.count);
            builder.append(")\n");

            if (i == items.size() - 1 || !items.get(i + 1).chest.equals(item.chest)) {
                updateChest(item.chest, side);
                turnFront();
            }
        }
        items.clear();
    }

    public void goTo(int positionX, int positionY, int positionZ) {
        builder.append("movement.goTo(");
        builder.append(positionX);
        builder.append(',');
        builder.append(positionY);
        builder.append(',');
        builder.append(positionZ);
        builder.append(")\n");
    }

    @SuppressWarnings("ComparatorCombinators") // items.sort requires Android N
    public void sortItems(List<Item> items, List<Chest> chests) {
        Collections.sort(chests, (o1, o2) -> o1.positionX - o2.positionX);
        final List<SortEntry> sort = new ArrayList<>();
        for (Item item : items) {
            for (Chest chest : chests) {
                if(chest.getFreeSpaceSlots().size() > 0) {
                    sort.add(new SortEntry(chest, item, chest.getFreeSpaceSlots().remove(0)));
                    break;
                }
            }
        }

        for (int i = 0; i < sort.size(); i++) {
            final SortEntry entry = sort.get(i);
            final Chest chest = entry.getChest();
            final Item item = entry.getItem();

            Side side = chest.side;
            if (i == 0 || !sort.get(i - 1).getChest().equals(chest)) {
                goTo(chest.positionX, chest.positionY, chest.positionZ);
                side = turnToSide(chest.side);
            } else if (side.isInaccessible()) {
                side = Side.FRONT;
            }

            builder.append("robot.select(");
            builder.append(i + 1);
            builder.append(")\n");

            builder.append("component.inventory_controller.dropIntoSlot(");
            builder.append(side.ordinal());
            builder.append(',');
            builder.append(entry.slot);
            builder.append(',');
            builder.append(item.count);
            builder.append(")\n");

            if (i == sort.size() - 1 || !sort.get(i + 1).getChest().equals(chest)) {
                updateChest(chest, side);
                turnFront();
            }
        }
    }

    public void updateChest(Chest chest) {
        goTo(chest.positionX, chest.positionY, chest.positionZ);
        Side checkSide = turnToSide(chest.side);
        updateChest(chest, checkSide);
        turnFront();
    }

    public void updateChest(Chest chest, Side checkSide) {
        builder.append("update(\"");
        builder.append(chest.id);
        builder.append("\",");
        builder.append(checkSide.ordinal());
        builder.append(',');
        builder.append(chest.side.ordinal());
        builder.append(',');
        builder.append(chest.positionX);
        builder.append(',');
        builder.append(chest.positionY);
        builder.append(',');
        builder.append(chest.positionZ);
        builder.append(");\n");
    }

    public Side previous;

    public Side turnToSide(Side side) {
        previous = side;
        if (side == Side.LEFT) {
            builder.append("robot.turnLeft();\n");
            return Side.FRONT;
        } else if (side == Side.RIGHT) {
            builder.append("robot.turnRight();\n");
            return Side.FRONT;
        } else if (side == Side.BACK) {
            builder.append("robot.turnAround();\n");
            return Side.FRONT;
        }
        return side;
    }

    public void turnFront() {
        if (previous == Side.LEFT) {
            builder.append("robot.turnRight();\n");
        } else if (previous == Side.RIGHT) {
            builder.append("robot.turnLeft();\n");
        } else if (previous == Side.BACK) {
            builder.append("robot.turnAround();\n");
        }
    }

    @NonNull
    @Override
    public String toString() {
        return builder.toString();
    }

    static class SortEntry {
        private final Chest chest;
        private final Item item;
        private final int slot;

        public SortEntry(Chest chest, Item item, int slot) {
            this.chest = chest;
            this.item = item;
            this.slot = slot;
        }

        public Chest getChest() {
            return chest;
        }

        public Item getItem() {
            return item;
        }

        public int getSlot() {
            return slot;
        }
    }
}
