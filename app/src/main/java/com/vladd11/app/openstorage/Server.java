package com.vladd11.app.openstorage;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.vladd11.app.openstorage.ui.ItemAdapter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    public static final String TAG = "Server";
    private final List<Item> requestList = new ArrayList<>();
    private final List<Chest> updateList = new ArrayList<>();
    private final List<Chest> chestList = new ArrayList<>();
    private final List<Item> internalItems = new ArrayList<>();
    private final List<Item> itemList = new ArrayList<>();
    private final TextureAtlas atlas;
    private final MutableLiveData<Boolean> isDelivered;
    private boolean sort;
    private ItemAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public Server(TextureAtlas atlas, MutableLiveData<Boolean> isDelivered) {
        super(44444);
        this.atlas = atlas;
        this.isDelivered = isDelivered;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            if (session.getMethod() == Method.GET) {
                final ProgramGenerator generator = new ProgramGenerator();
                generator.takeItems(requestList);
                for (Chest chest : updateList) {
                    generator.updateChest(chest);
                }
                if (sort && !internalItems.isEmpty() && !chestList.isEmpty()) {
                    generator.sortItems(internalItems, chestList);
                    sort = false;
                    handler.post(() -> isDelivered.setValue(true));
                }
                updateList.clear();
                generator.listItems();
                return newFixedLengthResponse(generator.toString());
            } else if (session.getMethod() == Method.POST) {
                itemList.clear();
                chestList.clear();
                internalItems.clear();

                int contentLength = Integer.parseInt(Objects.requireNonNull(session.getHeaders().get("content-length")));
                byte[] buffer = new byte[contentLength];
                // Value already in buffer
                //noinspection ResultOfMethodCallIgnored
                session.getInputStream().read(buffer, 0, contentLength);
                final String result = new String(buffer);

                final List<String> arr = new LinkedList<>(Arrays.asList(result.split(Pattern.quote("|"))));

                Scanner scanner = new Scanner(arr.get(0).trim());
                for (int position = 0; scanner.hasNextLine(); position++) {
                    final String line = scanner.nextLine();
                    final String[] sep = line.split(" ");
                    final String itemName = sep[0];
                    final int count = Integer.parseInt(sep[1]);

                    internalItems.add(new Item(itemName,
                            null,
                            count,
                            null,
                            joinAllFromIndex(sep, 3),
                            position + 1));
                }
                scanner.close();
                arr.remove(0);

                for (int i = 0; i < arr.size(); i++) {
                    //itemsMap.put(arr[i - 1], arr[i]);
                    scanner = new Scanner(arr.get(i).trim());
                    final String storageId = scanner.nextLine();
                    final String[] sepChest = scanner.nextLine().split(" ");
                    Log.d(TAG, "serve: " + Arrays.toString(sepChest));
                    final Chest currentChest = new Chest(Integer.parseInt(sepChest[0]),
                            Integer.parseInt(sepChest[1]),
                            Integer.parseInt(sepChest[2]),
                            storageId,
                            Side.values()[Integer.parseInt(sepChest[3])]);
                    chestList.add(currentChest);

                    for (int position = 0; scanner.hasNextLine(); position++) {
                        final String line = scanner.nextLine();
                        final String[] sep = line.split(" ");
                        final String itemName = sep[0];
                        if (itemName.equals("minecraft:air")) {
                            currentChest.addFreeSpaceSlot(position + 1);
                            continue;
                        }
                        final int count = Integer.parseInt(sep[1]);
                        final boolean isNBT = Boolean.parseBoolean(sep[2]);

                        // The item have additional info (that can change texture) and I can't check it.
                        // I won't try to display textures for these items
                        if (isNBT) {
                            itemList.add(new Item(itemName,
                                    null,
                                    count,
                                    currentChest,
                                    joinAllFromIndex(sep, 3),
                                    position + 1));
                        } else {
                            try {
                                itemList.add(new Item(itemName,
                                        atlas.loadTexture(itemName),
                                        count,
                                        currentChest,
                                        joinAllFromIndex(sep, 3),
                                        position + 1)); // In lua array index start from 1
                            } catch (IOException e) {
                                itemList.add(new Item(itemName,
                                        null,
                                        count,
                                        currentChest,
                                        joinAllFromIndex(sep, 3),
                                        position + 1));
                                e.printStackTrace();
                            }
                        }
                    }
                    scanner.close();
                }
                handler.post(() -> adapter.notifyDataSetChanged()); // TODO: rework it

                return newFixedLengthResponse("OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("");
    }

    public static String joinAllFromIndex(String[] array, int from) {
        final StringBuilder sb = new StringBuilder();
        for (int i = from; i < array.length; i++) {
            sb.append(array[i]);
            sb.append(" ");
        }
        return sb.toString();
    }

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
    } // TODO: rework it

    @NonNull
    public List<Item> getLastItemList() {
        return itemList;
    }

    public void addItemRequest(Item item) {
        requestList.add(item);
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public void addUpdateRequest(Chest chest) {
        updateList.add(chest);
    }
}