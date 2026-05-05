package com.example.androidphotos.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Serializable album with a name and ordered photos.
 */
public class Album implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = normalizeRequired(name, "name");
        this.photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalizeRequired(name, "name");
    }

    public List<Photo> getPhotos() {
        ensurePhotos();
        return new ArrayList<>(photos);
    }

    public boolean addPhoto(Photo photo) {
        ensurePhotos();
        if (photo == null) {
            throw new IllegalArgumentException("photo cannot be null.");
        }
        if (findPhotoByUri(photo.getUriString()) != null) {
            return false;
        }
        photos.add(photo);
        return true;
    }

    public boolean removePhotoByUri(String uriString) {
        ensurePhotos();
        Photo photo = findPhotoByUri(uriString);
        if (photo == null) {
            return false;
        }
        return photos.remove(photo);
    }

    public Photo findPhotoByUri(String uriString) {
        ensurePhotos();
        String normalizedUri = normalizeRequired(uriString, "uriString");
        for (Photo photo : photos) {
            if (photo.getUriString().equals(normalizedUri)) {
                return photo;
            }
        }
        return null;
    }

    private void ensurePhotos() {
        if (photos == null) {
            photos = new ArrayList<>();
        }
    }

    private static String normalizeRequired(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        return trimmed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Album album)) {
            return false;
        }
        return name.equalsIgnoreCase(album.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
}
