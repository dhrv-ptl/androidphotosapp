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
 * Serializable photo record for the Android port.
 */
public class Photo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String uriString;
    private String displayName;
    private List<Tag> tags;

    public Photo(String uriString, String displayName) {
        this.uriString = normalizeRequired(uriString, "uriString");
        this.displayName = normalizeRequired(displayName, "displayName");
        this.tags = new ArrayList<>();
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = normalizeRequired(uriString, "uriString");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = normalizeRequired(displayName, "displayName");
    }

    public List<Tag> getTags() {
        ensureTags();
        return new ArrayList<>(tags);
    }

    public boolean addTag(String type, String value) {
        ensureTags();
        Tag tag = new Tag(type, value);
        if (tags.contains(tag)) {
            return false;
        }
        tags.add(tag);
        return true;
    }

    public boolean removeTag(String type, String value) {
        ensureTags();
        return tags.remove(new Tag(type, value));
    }

    public boolean hasTag(String type, String value) {
        ensureTags();
        return tags.contains(new Tag(type, value));
    }

    public List<Tag> getTagsByType(String type) {
        ensureTags();
        String normalizedType = normalizeType(type);
        List<Tag> matches = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getType().equals(normalizedType)) {
                matches.add(tag);
            }
        }
        return matches;
    }

    public boolean matchesTag(String type, String value) {
        ensureTags();
        for (Tag tag : tags) {
            if (tag.matches(type, value)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getAutocompleteValues(String type, String prefix) {
        ensureTags();
        String normalizedType = normalizeType(type);
        String normalizedPrefix = prefix == null ? "" : prefix.trim().toLowerCase(Locale.ROOT);
        Set<String> matches = new LinkedHashSet<>();
        for (Tag tag : tags) {
            if (tag.getType().equals(normalizedType)
                    && tag.getValue().toLowerCase(Locale.ROOT).startsWith(normalizedPrefix)) {
                matches.add(tag.getValue());
            }
        }
        return matches;
    }

    private void ensureTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
    }

    private static String normalizeType(String type) {
        if (!Tag.isValidType(type)) {
            throw new IllegalArgumentException("Tag type must be person or location.");
        }
        return type.trim().toLowerCase(Locale.ROOT);
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
        if (!(other instanceof Photo photo)) {
            return false;
        }
        return uriString.equals(photo.uriString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uriString);
    }
}
