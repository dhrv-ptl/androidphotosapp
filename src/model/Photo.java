package model;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Stores one photo's shared metadata for a user.
 *
 * @author Dhruv Patel
 */
public class Photo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String absoluteFilePath;
    private String caption = "";
    private final LocalDate photoDate;
    private final Set<Tag> tags = new LinkedHashSet<>();

    /**
     * Creates a photo from a file path.
     *
     * @param absoluteFilePath absolute file path
     */
    public Photo(String absoluteFilePath) {
        this.absoluteFilePath = normalizeAbsolutePath(absoluteFilePath);
        this.photoDate = readPhotoDate(this.absoluteFilePath);
    }

    /**
     * Returns the absolute file path.
     *
     * @return absolute file path
     */
    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    /**
     * Returns the caption.
     *
     * @return caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption.
     *
     * @param caption caption
     */
    public void setCaption(String caption) {
        this.caption = caption == null ? "" : caption.trim();
    }

    /**
     * Returns the photo date from the filesystem.
     *
     * @return photo date
     */
    public LocalDate getPhotoDate() {
        return photoDate;
    }

    /**
     * Returns all tags.
     *
     * @return tags
     */
    public Set<Tag> getTags() {
        return new LinkedHashSet<>(tags);
    }

    /**
     * Adds a tag to the photo.
     *
     * @param definition tag type
     * @param value tag value
     * @return true if added
     */
    public boolean addTag(TagDefinition definition, String value) {
        if (definition == null) {
            throw new IllegalArgumentException("definition cannot be null.");
        }

        String normalizedValue = normalizeRequired(value, "value");
        if (!definition.isMultiValue() && hasTagType(definition.getName())) {
            return false;
        }
        return tags.add(new Tag(definition, normalizedValue));
    }

    /**
     * Removes a tag from the photo.
     *
     * @param definition tag type
     * @param value tag value
     * @return true if removed
     */
    public boolean removeTag(TagDefinition definition, String value) {
        if (definition == null) {
            return false;
        }
        return tags.remove(new Tag(definition, value));
    }

    /**
     * Returns whether the photo has a tag type.
     *
     * @param tagTypeName tag type name
     * @return true if present
     */
    public boolean hasTagType(String tagTypeName) {
        String normalizedType = normalizeRequired(tagTypeName, "tagTypeName");
        for (Tag tag : tags) {
            if (tag.getTypeName().equals(normalizedType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the photo has the exact tag.
     *
     * @param definition tag type
     * @param value tag value
     * @return true if present
     */
    public boolean hasTag(TagDefinition definition, String value) {
        if (definition == null) {
            return false;
        }
        return tags.contains(new Tag(definition, value));
    }

    /**
     * Removes all tags of one tag type from the photo.
     *
     * @param tagTypeName tag type name
     * @return true if any tags were removed
     */
    public boolean removeTagsByType(String tagTypeName) {
        String normalizedType = normalizeRequired(tagTypeName, "tagTypeName");
        boolean removed = false;
        Set<Tag> currentTags = new LinkedHashSet<>(tags);
        for (Tag tag : currentTags) {
            if (tag.getTypeName().equals(normalizedType)) {
                removed |= tags.remove(tag);
            }
        }
        return removed;
    }

    private static LocalDate readPhotoDate(String absoluteFilePath) {
        Path path = Path.of(absoluteFilePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Photo file does not exist.");
        }

        try {
            Instant instant = Files.getLastModifiedTime(path).toInstant();
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to read photo date.", exception);
        }
    }

    private static String normalizeAbsolutePath(String absoluteFilePath) {
        String trimmed = normalizeRequired(absoluteFilePath, "absoluteFilePath");
        return Path.of(trimmed).toAbsolutePath().normalize().toString();
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
     * Compares photos by absolute file path.
     *
     * @param other other object
     * @return true if paths match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Photo photo)) {
            return false;
        }
        return absoluteFilePath.equals(photo.absoluteFilePath);
    }

    /**
     * Returns a hash code based on absolute file path.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(absoluteFilePath);
    }

    /**
     * Returns the absolute file path.
     *
     * @return absolute file path
     */
    @Override
    public String toString() {
        return absoluteFilePath;
    }
}
