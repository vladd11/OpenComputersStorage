package com.vladd11.app.openstorage.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.vladd11.app.openstorage.Chest;
import com.vladd11.app.openstorage.R;
import com.vladd11.app.openstorage.Server;
import com.vladd11.app.openstorage.Side;

public class ChestUpdateDialog extends DialogFragment {
    private final Server server;

    public ChestUpdateDialog(Server server) {
        this.server = server;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Activity activity = requireActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater layoutInflater = activity.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.update_dialog, null);

        final EditText idEditText = view.findViewById(R.id.idEditText);
        final EditText xCoordinateEditText = view.findViewById(R.id.xCoordinateEditText);
        final EditText yCoordinateEditText = view.findViewById(R.id.yCoordinateEditText);
        final EditText zCoordinateEditText = view.findViewById(R.id.zCoordinateEditText);

        final Spinner spinner = view.findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.sides,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Side[] side = new Side[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                side[0] = Side.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setView(view).setTitle("Add or update chest")
                .setPositiveButton("Send", (dialog, which) -> server.addUpdateRequest(new Chest(
                        Integer.parseInt(xCoordinateEditText.getText().toString()),
                        Integer.parseInt(yCoordinateEditText.getText().toString()),
                        Integer.parseInt(zCoordinateEditText.getText().toString()),
                        idEditText.getText().toString(),
                        side[0]
                )))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder.create();
    }
}
