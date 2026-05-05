package com.example.androidphotos;

import android.view.View;
import android.widget.AdapterView;

/**
 * Small helper so callers only implement selection handling.
 */
public abstract class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public abstract void onSelectionChanged();

    @Override
    public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onSelectionChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
