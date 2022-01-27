package com.vladd11.app.openstorage;

import android.text.TextUtils;

import androidx.databinding.InverseMethod;

public class Converter {
    @InverseMethod("convertIntegerToString")
    public static int convertStringToInteger(String value) {
        if (TextUtils.isEmpty(value) || !TextUtils.isDigitsOnly(value)) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    public static String convertIntegerToString(int value) {
        return String.valueOf(value);
    }
}