package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Defines one tag type.
 *
 * @author Dhruv Patel
 */
public class TagDefinition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final boolean multiValue;
    private final boolean preset;

    /**
     * Creates a tag definition.
     *
     * @param name tag type name
     * @param multiValue true for multi-value tags
     * @param preset true if built in
     */
    public TagDefinition(String name, boolean multiValue, boolean preset) {
        this.name = normalizeRequired(name, "name");
        this.multiValue = multiValue;
        this.preset = preset;
    }

    /**
     * Returns the tag type name.
     *
     * @return tag type name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the tag type supports multiple values.
     *
     * @return true if multi-value
     */
    public boolean isMultiValue() {
        return multiValue;
    }

    /**
     * Returns whether the tag type is built in.
     *
     * @return true if preset
     */
    public boolean isPreset() {
        return preset;
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
     * Compares tag definitions by name.
     *
     * @param other other object
     * @return true if names match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TagDefinition definition)) {
            return false;
        }
        return name.equals(definition.name);
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
     * Returns the tag type name.
     *
     * @return tag type name
     */
    @Override
    public String toString() {
        return name;
    }
}
