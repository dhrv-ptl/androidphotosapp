package com.example.androidphotos.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Serializable photo tag limited to the supported Android assignment types.
 */
public class Tag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String TYPE_PERSON = "person";
    public static final String TYPE_LOCATION = "location";

    private static final Set<String> VALID_TYPES = Set.of(TYPE_PERSON, TYPE_LOCATION);

    private String type;
    private String value;

    public Tag(String type, String value) {
        this.type = normalizeType(type);
        this.value = normalizeRequired(value, "value");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = normalizeType(type);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = normalizeRequired(value, "value");
    }

    public boolean matches(String expectedType, String expectedValue) {
        if (expectedType == null || expectedValue == null) {
            return false;
        }
        return type.equals(normalizeType(expectedType))
                && value.equalsIgnoreCase(expectedValue.trim());
    }

    public boolean startsWithValue(String expectedType, String prefix) {
        if (expectedType == null || prefix == null) {
            return false;
        }
        return type.equals(normalizeType(expectedType))
                && value.toLowerCase(Locale.ROOT).startsWith(prefix.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isValidType(String type) {
        if (type == null) {
            return false;
        }
        return VALID_TYPES.contains(type.trim().toLowerCase(Locale.ROOT));
    }

    private static String normalizeType(String type) {
        String normalized = normalizeRequired(type, "type").toLowerCase(Locale.ROOT);
        if (!VALID_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Tag type must be person or location.");
        }
        return normalized;
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
        if (!(other instanceof Tag tag)) {
            return false;
        }
        return type.equals(tag.type) && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value.toLowerCase(Locale.ROOT));
    }
}
