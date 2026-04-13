package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Stores one tag on a photo.
 *
 * @author Dhruv Patel
 */
public class Tag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final TagDefinition definition;
    private final String value;

    /**
     * Creates a tag.
     *
     * @param definition tag type
     * @param value tag value
     */
    public Tag(TagDefinition definition, String value) {
        if (definition == null) {
            throw new IllegalArgumentException("definition cannot be null.");
        }
        this.definition = definition;
        this.value = normalizeRequired(value, "value");
    }

    /**
     * Returns the tag definition.
     *
     * @return tag definition
     */
    public TagDefinition getDefinition() {
        return definition;
    }

    /**
     * Returns the tag type name.
     *
     * @return tag type name
     */
    public String getTypeName() {
        return definition.getName();
    }

    /**
     * Returns the tag value.
     *
     * @return tag value
     */
    public String getValue() {
        return value;
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
     * Compares tags by tag type and value.
     *
     * @param other other object
     * @return true if type and value match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Tag tag)) {
            return false;
        }
        return definition.equals(tag.definition) && value.equals(tag.value);
    }

    /**
     * Returns a hash code based on tag type and value.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(definition, value);
    }

    /**
     * Returns a display string for the tag.
     *
     * @return display text
     */
    @Override
    public String toString() {
        return definition.getName() + "=" + value;
    }
}
