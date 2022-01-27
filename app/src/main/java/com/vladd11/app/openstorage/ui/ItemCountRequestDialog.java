package com.vladd11.app.openstorage.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.vladd11.app.openstorage.BR;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.databinding.ItemCountDialogBinding;
import com.vladd11.app.openstorage.utils.Item;
import com.vladd11.app.openstorage.utils.ItemRequest;

public class ItemCountRequestDialog extends DialogFragment {
    private final Item item;
    private ItemCountDialogBinding binding;
    private ItemCountRequestDialogListener listener;

    public ItemCountRequestDialog(Item item) {
        this.item = item;
    }

    public interface ItemCountRequestDialogListener {
        void onAddToRequestClicked(ItemRequest request);

        void onRequestNowClicked(ItemRequest request);
    }

    public ItemCountRequestDialog setListener(ItemCountRequestDialogListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Activity activity = requireActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //final View view = layoutInflater.inflate(R.layout.item_count_dialog, null);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.item_count_dialog, null, false);

        final ItemRequest itemRequest = new ItemRequest(item, 1);
        binding.setItemRequest(itemRequest);

        builder.setPositiveButton(R.string.request_add, (dialog, which) -> listener.onAddToRequestClicked(itemRequest));
        builder.setNeutralButton(R.string.request_now, (dialog, which) -> listener.onRequestNowClicked(itemRequest));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.setView(binding.getRoot());

        final AlertDialog dialog = builder.create();
        BindingUtil.bindTextChange(binding, binding.itemCountEditText, BR.errorText, e -> {
            final String value = e.toString();
            if (!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)) {
                final int intValue = Integer.parseInt(value);
                if(intValue > 0 && intValue < 65) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
                    return false;
                }
            }

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
            return true;
        }, "Count should be in 1-64 range");
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
