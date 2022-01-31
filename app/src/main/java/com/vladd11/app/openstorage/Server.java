package com.vladd11.app.openstorage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.vladd11.app.openstorage.utils.Chest;
import com.vladd11.app.openstorage.utils.Item;
import com.vladd11.app.openstorage.utils.ItemRequest;
import com.vladd11.app.openstorage.utils.ServerState;
import com.vladd11.app.openstorage.utils.Side;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {
    public static final String TAG = "Server";
    private final List<ItemRequest> requestList = new ArrayList<>();
    private final List<Chest> updateList = new ArrayList<>();
    private final List<Chest> chestList = new ArrayList<>();
    private final List<Item> internalItems = new ArrayList<>();
    private List<Item> oldItemList = new ArrayList<>();
    private final TextureAtlas atlas;
    private ServerListener listener;
    private final ServerState serverState;

    public Server(TextureAtlas atlas) {
        super(44444);
        serverState = new ServerState(false, false);
        this.atlas = atlas;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            if (session.getMethod() == Method.GET) {
                final ProgramGenerator generator = new ProgramGenerator();

                if (serverState.isRequest()) {
                    generator.takeItems(requestList);
                    serverState.setRequest(false);
                }

                for (Chest chest : updateList) {
                    generator.updateChest(chest);
                }

                if (serverState.isSort() && !internalItems.isEmpty() && !chestList.isEmpty()) {
                    generator.sortItems(internalItems, chestList);
                    serverState.setSort(false);
                }

                updateList.clear();
                generator.listItems();
                listener.onCommandsDelivered();
                return newFixedLengthResponse(generator.toString());
            } else if (session.getMethod() == Method.POST) {
                final List<Item> itemList = new ArrayList<>();
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
                for (int position = 1; scanner.hasNextLine(); position++) { // In lua arrays start from 1
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

                    for (int position = 1; scanner.hasNextLine(); position++) {
                        final String line = scanner.nextLine();
                        final String[] sep = line.split(" ");
                        final String itemName = sep[0];
                        if (itemName.equals("minecraft:air")) {
                            currentChest.addFreeSpaceSlot(position);
                            continue;
                        }
                        final int count = Integer.parseInt(sep[1]);
                        final boolean isNBT = Boolean.parseBoolean(sep[2]);
                        final String title = joinAllFromIndex(sep, 3);

                        try {
                            final Item item = new Item(itemName,
                                    atlas.loadTexture(itemName, title),
                                    count,
                                    currentChest,
                                    joinAllFromIndex(sep, 3),
                                    position);
                            itemList.add(item);
                            currentChest.addItem(item);
                        } catch (IOException e) {
                            final Item item = new Item(itemName,
                                    null,
                                    count,
                                    currentChest,
                                    title,
                                    position);
                            itemList.add(item);
                            currentChest.addItem(item);
                            e.printStackTrace();
                        }
                    }
                    scanner.close();
                }

                if (oldItemList.size() == 0 || !oldItemList.containsAll(itemList) || !itemList.containsAll(oldItemList)) {
                    listener.onItemsUpdated(itemList);
                }
                oldItemList = itemList;

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

    public interface ServerListener {
        void onItemsUpdated(List<Item> items);

        void onCommandsDelivered();
    }

    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    @NonNull
    public List<Item> getLastItemList() {
        return oldItemList;
    }

    public void addItemRequest(ItemRequest itemRequest) {
        requestList.add(itemRequest);
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void addUpdateRequest(Chest chest) {
        updateList.add(chest);
    }
}