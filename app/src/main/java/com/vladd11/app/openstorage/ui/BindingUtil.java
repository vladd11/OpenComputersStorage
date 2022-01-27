package com.vladd11.app.openstorage.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ViewDataBinding;

import com.vladd11.app.openstorage.databinding.ItemCountDialogBinding;

public class BindingUtil {
    public interface StringRule {
        boolean validate(Editable s);
    }

    public static void bindTextChange(final ViewDataBinding binding, final EditText editText, int variableId, final StringRule pStringRule, final String msg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pStringRule.validate(s)) {
                    binding.setVariable(variableId, msg);
                }
            }
        });
    }
}
