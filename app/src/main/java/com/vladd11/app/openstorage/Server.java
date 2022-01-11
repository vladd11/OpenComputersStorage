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
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    public static final String TAG = "Server";
    private final Queue<Item> itemQueue = new ArrayDeque<>();
    private final Queue<Chest> updateQueue = new ArrayDeque<>();
    private final TextureAtlas atlas;
    private final MutableLiveData<Boolean> isDelivered;
    private boolean sort;
    private final List<Item> itemList = new ArrayList<>();
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
                final StringBuilder sb = new StringBuilder();
                while (!itemQueue.isEmpty()) {
                    final Item item = itemQueue.poll();
                    assert item != null;
                    sb.append("take ");
                    sb.append(item.chest.positionX);
                    sb.append(" ");
                    sb.append(item.chest.positionY);
                    sb.append(" ");
                    sb.append(item.chest.positionZ);
                    sb.append(" ");
                    sb.append(item.chest.side);
                    sb.append(" ");
                    sb.append(item.position);
                    sb.append(" ");
                    sb.append(item.count);
                    sb.append(" ");
                    sb.append(item.chest.id);
                    sb.append("\n");
                }

                while (!updateQueue.isEmpty()) {
                    final Chest chest = updateQueue.poll();
                    assert chest != null;
                    sb.append("update ");
                    sb.append(chest.id);
                    sb.append(" ");
                    sb.append(chest.side);
                    sb.append(" ");
                    sb.append(chest.positionX);
                    sb.append(" ");
                    sb.append(chest.positionY);
                    sb.append(" ");
                    sb.append(chest.positionZ);
                    sb.append("\n");
                }

                if (sort) {
                    sort = false;
                    sb.append("sort\n");
                }

                if (itemList.isEmpty()) sb.append("list");
                handler.post(() -> isDelivered.setValue(true));

                return newFixedLengthResponse(sb.toString());
            } else if (session.getMethod() == Method.POST) {
                final Scanner s = new Scanner(session.getInputStream()).useDelimiter("\\A");
                final String result = s.hasNext() ? s.next() : "";

                final List<String> arr = new LinkedList<>(Arrays.asList(result.split(Pattern.quote("|"))));
                arr.remove(0);
                for (int i = 0; i < arr.size(); i++) {
                    //itemsMap.put(arr[i - 1], arr[i]);
                    final Scanner scanner = new Scanner(arr.get(i).trim());
                    final String storageId = scanner.nextLine();
                    final String[] sepChest = scanner.nextLine().split(" ");
                    Log.d(TAG, "serve: " + Arrays.toString(sepChest));
                    final Chest currentChest = new Chest(Integer.parseInt(sepChest[0]),
                            Integer.parseInt(sepChest[1]),
                            Integer.parseInt(sepChest[2]),
                            storageId,
                            Integer.parseInt(sepChest[3]));

                    for (int position = 0; scanner.hasNextLine(); position++) {
                        final String line = scanner.nextLine();
                        final String[] sep = line.split(" ");
                        final String itemName = sep[0];
                        if (itemName.equals("minecraft:air")) continue;
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
                }
                handler.post(() -> adapter.notifyDataSetChanged()); // TODO: rework it

                return newFixedLengthResponse("OK");
            } else
                return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "");
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
        itemQueue.offer(item);
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public void addUpdateRequest(Chest chest) {
        updateQueue.offer(chest);
    }
}