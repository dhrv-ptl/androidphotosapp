package model;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Stores one user's albums and photo library.
 *
 * @author Dhruv Patel
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Map<String, Album> albumsByName = new LinkedHashMap<>();
    private final Map<String, Photo> photosByPath = new LinkedHashMap<>();

    /**
     * Creates a user.
     *
     * @param username username
     */
    public User(String username) {
        this.username = normalizeRequired(username, "username");
    }

    /**
     * Returns the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns all albums in insertion order.
     *
     * @return albums
     */
    public List<Album> getAlbums() {
        return new ArrayList<>(albumsByName.values());
    }

    /**
     * Returns all photos owned by this user.
     *
     * @return photos
     */
    public List<Photo> getPhotos() {
        return new ArrayList<>(photosByPath.values());
    }

    /**
     * Finds an album by name.
     *
     * @param albumName album name
     * @return matching album or null
     */
    public Album getAlbum(String albumName) {
        return albumsByName.get(normalizeRequired(albumName, "albumName"));
    }

    /**
     * Returns whether the user already has an album name.
     *
     * @param albumName album name
     * @return true if it exists
     */
    public boolean hasAlbum(String albumName) {
        return albumsByName.containsKey(normalizeRequired(albumName, "albumName"));
    }

    /**
     * Creates a new album.
     *
     * @param albumName album name
     * @return created album
     */
    public Album createAlbum(String albumName) {
        String normalizedName = normalizeRequired(albumName, "albumName");
        if (albumsByName.containsKey(normalizedName)) {
            throw new IllegalArgumentException("Album already exists.");
        }

        Album album = new Album(normalizedName);
        albumsByName.put(normalizedName, album);
        return album;
    }

    /**
     * Deletes an album.
     *
     * @param albumName album name
     * @return true if removed
     */
    public boolean deleteAlbum(String albumName) {
        return albumsByName.remove(normalizeRequired(albumName, "albumName")) != null;
    }

    /**
     * Renames an album.
     *
     * @param oldName current name
     * @param newName new name
     * @return true if renamed
     */
    public boolean renameAlbum(String oldName, String newName) {
        String currentName = normalizeRequired(oldName, "oldName");
        String replacementName = normalizeRequired(newName, "newName");

        Album album = albumsByName.get(currentName);
        if (album == null) {
            return false;
        }
        if (!currentName.equals(replacementName) && albumsByName.containsKey(replacementName)) {
            throw new IllegalArgumentException("Album already exists.");
        }

        albumsByName.remove(currentName);
        album.setName(replacementName);
        albumsByName.put(replacementName, album);
        return true;
    }

    /**
     * Finds a photo in the user's library by absolute path.
     *
     * @param absolutePath photo path
     * @return matching photo or null
     */
    public Photo getPhoto(String absolutePath) {
        return photosByPath.get(normalizeAbsolutePath(absolutePath));
    }

    /**
     * Adds a photo to an album. Existing photo metadata is shared across albums.
     *
     * @param albumName album name
     * @param absolutePath absolute photo path
     * @return the shared photo object
     */
    public Photo addPhotoToAlbum(String albumName, String absolutePath) {
        Album album = requireAlbum(albumName);
        String normalizedPath = normalizeAbsolutePath(absolutePath);

        Photo photo = photosByPath.get(normalizedPath);
        if (photo == null) {
            photo = new Photo(normalizedPath);
            photosByPath.put(normalizedPath, photo);
        }

        album.addPhoto(photo);
        return photo;
    }

    /**
     * Removes a photo from an album.
     *
     * @param albumName album name
     * @param absolutePath absolute photo path
     * @return true if removed
     */
    public boolean removePhotoFromAlbum(String albumName, String absolutePath) {
        Album album = requireAlbum(albumName);
        boolean removed = album.removePhotoByPath(absolutePath);
        if (removed) {
            removeUnusedPhoto(normalizeAbsolutePath(absolutePath));
        }
        return removed;
    }

    /**
     * Returns whether the user already has a photo path in the library.
     *
     * @param absolutePath absolute photo path
     * @return true if present
     */
    public boolean hasPhoto(String absolutePath) {
        return photosByPath.containsKey(normalizeAbsolutePath(absolutePath));
    }

    /**
     * Returns the album names in insertion order.
     *
     * @return album names
     */
    public List<String> getAlbumNames() {
        return new ArrayList<>(albumsByName.keySet());
    }

    private Album requireAlbum(String albumName) {
        Album album = getAlbum(albumName);
        if (album == null) {
            throw new IllegalArgumentException("Album does not exist.");
        }
        return album;
    }

    private void removeUnusedPhoto(String normalizedPath) {
        Collection<Album> albums = albumsByName.values();
        for (Album album : albums) {
            if (album.containsPhoto(normalizedPath)) {
                return;
            }
        }
        photosByPath.remove(normalizedPath);
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

    private static String normalizeAbsolutePath(String absolutePath) {
        String trimmed = normalizeRequired(absolutePath, "absolutePath");
        return Path.of(trimmed).toAbsolutePath().normalize().toString();
    }

    /**
     * Compares users by username.
     *
     * @param other other object
     * @return true if usernames match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User user)) {
            return false;
        }
        return username.equals(user.username);
    }

    /**
     * Returns a hash code based on username.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Returns the username.
     *
     * @return username
     */
    @Override
    public String toString() {
        return username;
    }
}
