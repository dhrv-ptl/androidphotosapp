package model;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Stores one album and its photo references.
 *
 * @author Dhruv Patel
 */
public class Album implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private final Set<Photo> photos = new LinkedHashSet<>();

    /**
     * Creates an album.
     *
     * @param name album name
     */
    public Album(String name) {
        setName(name);
    }

    /**
     * Returns the album name.
     *
     * @return album name
     */
    public String getName() {
        return name;
    }

    /**
     * Renames the album.
     *
     * @param name new album name
     */
    public void setName(String name) {
        this.name = normalizeRequired(name, "name");
    }

    /**
     * Returns photos in insertion order.
     *
     * @return photos
     */
    public List<Photo> getPhotos() {
        return new ArrayList<>(photos);
    }

    /**
     * Adds a photo if it is not already in the album.
     *
     * @param photo photo to add
     * @return true if added
     */
    public boolean addPhoto(Photo photo) {
        if (photo == null) {
            throw new IllegalArgumentException("photo cannot be null.");
        }
        return photos.add(photo);
    }

    /**
     * Removes a photo from the album.
     *
     * @param photo photo to remove
     * @return true if removed
     */
    public boolean removePhoto(Photo photo) {
        if (photo == null) {
            return false;
        }
        return photos.remove(photo);
    }

    /**
     * Removes a photo by path.
     *
     * @param absolutePath absolute photo path
     * @return true if removed
     */
    public boolean removePhotoByPath(String absolutePath) {
        Photo photo = getPhotoByPath(absolutePath);
        return photo != null && photos.remove(photo);
    }

    /**
     * Returns whether a photo path is already in the album.
     *
     * @param absolutePath absolute photo path
     * @return true if present
     */
    public boolean containsPhoto(String absolutePath) {
        return getPhotoByPath(absolutePath) != null;
    }

    /**
     * Finds a photo by absolute path inside the album.
     *
     * @param absolutePath absolute photo path
     * @return matching photo or null
     */
    public Photo getPhotoByPath(String absolutePath) {
        String normalizedPath = Path.of(normalizeRequired(absolutePath, "absolutePath"))
                .toAbsolutePath()
                .normalize()
                .toString();
        for (Photo photo : photos) {
            if (photo.getAbsoluteFilePath().equals(normalizedPath)) {
                return photo;
            }
        }
        return null;
    }

    /**
     * Returns the number of photos in the album.
     *
     * @return photo count
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Returns the earliest photo date in the album.
     *
     * @return earliest date if any
     */
    public Optional<LocalDate> getEarliestPhotoDate() {
        return photos.stream()
                .map(Photo::getPhotoDate)
                .min(LocalDate::compareTo);
    }

    /**
     * Returns the latest photo date in the album.
     *
     * @return latest date if any
     */
    public Optional<LocalDate> getLatestPhotoDate() {
        return photos.stream()
                .map(Photo::getPhotoDate)
                .max(LocalDate::compareTo);
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

    /**
     * Compares albums by name.
     *
     * @param other other object
     * @return true if names match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Album album)) {
            return false;
        }
        return name.equals(album.name);
    }

    /**
     * Returns a hash code based on name.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns the album name.
     *
     * @return album name
     */
    @Override
    public String toString() {
        return name;
    }
}
