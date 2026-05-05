package com.example.androidphotos;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Small helper so callers only override the text callback they need.
 */
public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public abstract void afterTextChanged(Editable editable);
}
