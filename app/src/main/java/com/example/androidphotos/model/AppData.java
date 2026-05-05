package com.example.androidphotos.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Root serializable application data for the Android port.
 */
public class AppData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Album> albums;

    public AppData() {
        this.albums = new ArrayList<>();
    }

    public List<Album> getAlbums() {
        ensureAlbums();
        return new ArrayList<>(albums);
    }

    public Album getAlbum(String albumName) {
        ensureAlbums();
        String normalizedName = normalizeRequired(albumName, "albumName");
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(normalizedName)) {
                return album;
            }
        }
        return null;
    }

    public boolean hasAlbum(String albumName) {
        return getAlbum(albumName) != null;
    }

    public Album addAlbum(String albumName) {
        ensureAlbums();
        String normalizedName = normalizeRequired(albumName, "albumName");
        if (hasAlbum(normalizedName)) {
            throw new IllegalArgumentException("Album already exists.");
        }
        Album album = new Album(normalizedName);
        albums.add(album);
        return album;
    }

    public boolean deleteAlbum(String albumName) {
        ensureAlbums();
        Album album = getAlbum(albumName);
        if (album == null) {
            return false;
        }
        return albums.remove(album);
    }

    public boolean renameAlbum(String oldName, String newName) {
        Album album = getAlbum(oldName);
        if (album == null) {
            return false;
        }

        String normalizedNewName = normalizeRequired(newName, "newName");
        Album duplicate = getAlbum(normalizedNewName);
        if (duplicate != null && duplicate != album) {
            throw new IllegalArgumentException("Album already exists.");
        }

        album.setName(normalizedNewName);
        return true;
    }

    public boolean movePhoto(String sourceAlbumName, String targetAlbumName, String uriString) {
        Album sourceAlbum = getAlbum(sourceAlbumName);
        Album targetAlbum = getAlbum(targetAlbumName);
        if (sourceAlbum == null || targetAlbum == null) {
            return false;
        }

        Photo photo = sourceAlbum.findPhotoByUri(uriString);
        if (photo == null || targetAlbum.findPhotoByUri(uriString) != null) {
            return false;
        }

        sourceAlbum.removePhotoByUri(uriString);
        targetAlbum.addPhoto(photo);
        return true;
    }

    public List<Photo> searchBySingleTag(String type, String value) {
        ensureAlbums();
        List<Photo> matches = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.matchesTag(type, value) && !matches.contains(photo)) {
                    matches.add(photo);
                }
            }
        }
        return matches;
    }

    public List<Photo> searchByTwoTags(String firstType, String firstValue,
                                       String secondType, String secondValue,
                                       boolean matchAll) {
        ensureAlbums();
        List<Photo> matches = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean firstMatches = photo.matchesTag(firstType, firstValue);
                boolean secondMatches = photo.matchesTag(secondType, secondValue);
                boolean include = matchAll ? (firstMatches && secondMatches) : (firstMatches || secondMatches);
                if (include && !matches.contains(photo)) {
                    matches.add(photo);
                }
            }
        }
        return matches;
    }

    public List<String> getAutocompleteValues(String type, String prefix) {
        ensureAlbums();
        String normalizedPrefix = prefix == null ? "" : prefix.trim().toLowerCase(Locale.ROOT);
        Set<String> matches = new LinkedHashSet<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                matches.addAll(photo.getAutocompleteValues(type, normalizedPrefix));
            }
        }
        return new ArrayList<>(matches);
    }

    private void ensureAlbums() {
        if (albums == null) {
            albums = new ArrayList<>();
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
        if (!(other instanceof AppData appData)) {
            return false;
        }
        return Objects.equals(albums, appData.albums);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albums);
    }
}
