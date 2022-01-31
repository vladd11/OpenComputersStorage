package com.vladd11.app.openstorage.utils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vladd11.app.openstorage.BR;

public class ServerState extends BaseObservable {
    private boolean sort;
    private boolean request;

    public ServerState(boolean sort, boolean request) {
        this.sort = sort;
        this.request = request;
    }

    @Bindable
    public boolean isSort() {
        return sort;
    }

    public void setRequest(boolean request) {
        this.request = request;
        notifyPropertyChanged(BR.request);
    }

    @Bindable
    public boolean isRequest() {
        return request;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
        notifyPropertyChanged(BR.sort);
    }
}
