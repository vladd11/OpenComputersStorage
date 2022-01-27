package com.vladd11.app.openstorage.utils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vladd11.app.openstorage.BR;

public class ItemRequest extends BaseObservable {
    public final Item item;
    private int count;

    public ItemRequest(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}