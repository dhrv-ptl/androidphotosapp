package com.example.androidphotos.storage;

import android.content.Context;

import com.example.androidphotos.model.AppData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Saves and loads serialized application data using Android internal storage.
 */
public final class DataStore {

    public static final String FILE_NAME = "photos_data.ser";

    private DataStore() {
    }

    public static AppData load(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        }

        try (FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object object = objectInputStream.readObject();
            if (object instanceof AppData appData) {
                return appData;
            }
        } catch (IOException | ClassNotFoundException | RuntimeException exception) {
            return new AppData();
        }

        return new AppData();
    }

    public static void save(Context context, AppData data) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null.");
        }

        try (FileOutputStream fileOutputStream =
                     context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save application data.", exception);
        }
    }
}
